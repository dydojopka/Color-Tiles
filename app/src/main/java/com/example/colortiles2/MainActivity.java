package com.example.colortiles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int darkColor;
    int brightColor;
    TextView generationMovesText;

    // Первый индекс задает строку, второй задает столбец, счет начинается с нуля
    View[][] tiles = new View[4][4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Разметка игрового экрана
        setContentView(R.layout.activity_main);
        Resources r = getResources();
        darkColor = r.getColor(R.color.dark);
        brightColor = r.getColor(R.color.bright);

        // Список ходов генерации
        generationMovesText = findViewById(R.id.generation_moves);

        // Заполнение массива ссылками на плитки
        tiles[0][0] = findViewById(R.id.t00);
        tiles[0][1] = findViewById(R.id.t01);
        tiles[0][2] = findViewById(R.id.t02);
        tiles[0][3] = findViewById(R.id.t03);
        tiles[1][0] = findViewById(R.id.t10);
        tiles[1][1] = findViewById(R.id.t11);
        tiles[1][2] = findViewById(R.id.t12);
        tiles[1][3] = findViewById(R.id.t13);
        tiles[2][0] = findViewById(R.id.t20);
        tiles[2][1] = findViewById(R.id.t21);
        tiles[2][2] = findViewById(R.id.t22);
        tiles[2][3] = findViewById(R.id.t23);
        tiles[3][0] = findViewById(R.id.t30);
        tiles[3][1] = findViewById(R.id.t31);
        tiles[3][2] = findViewById(R.id.t32);
        tiles[3][3] = findViewById(R.id.t33);

        // Расчет ширины поля и высоты (квадрат)
        LinearLayout field = findViewById(R.id.field);
        field.post(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) field.getLayoutParams();
            params.height = field.getWidth();
            field.setLayoutParams(params);
        });

        generateField();
    }

    // Переключение цвета плитки
    public void changeColor(View v) {
        ColorDrawable d = (ColorDrawable) v.getBackground();

        if (d.getColor() == brightColor) {
            v.setBackgroundColor(darkColor);
        } else {
            v.setBackgroundColor(brightColor);
        }
    }

    // Переключение квадратов 3x3
    public void changeAround(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < 4 && j >= 0 && j < 4) { //
                    changeColor(tiles[i][j]);
                }
            }
        }
    }

    public void onClick(View v) {
        // Первая цифра тега задает строку, вторая задает столбец
        String tag = v.getTag().toString();
        int x = Integer.parseInt(tag.substring(0, 1));
        int y = Integer.parseInt(tag.substring(1, 2));

        changeAround(x, y);

        // После хода проверка на победу и показ сообщения
        if (checkWin()) {
            ColorDrawable d = (ColorDrawable) tiles[0][0].getBackground();
            int message;

            if (d.getColor() == darkColor) {
                message = R.string.win_dark;
            } else {
                message = R.string.win_bright;
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void generateField() {
        Random random = new Random();
        String lastStart = getPreferences(MODE_PRIVATE).getString("last_start", "");
        String start;
        String moves;

        // Повтор генерации при: уже было / уже выиграно
        do {
            // Очищаем список перед каждой попыткой генерации
            moves = "";
            int moveNumber = 1;
            int startColor;

            if (random.nextBoolean()) {
                startColor = brightColor;
            } else {
                startColor = darkColor;
            }

            // Начало - одноцветное поле
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    tiles[i][j].setBackgroundColor(startColor);
                }
            }

            // Случайные ходы и их запись в список
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (random.nextBoolean()) {
                        changeAround(i, j);
                        moves += getString(
                                R.string.generation_move,
                                moveNumber,
                                i + 1,
                                j + 1
                        ) + "\n";
                        moveNumber++;
                    }
                }
            }

            // Записываем полученную стартовую расстановку
            start = "";
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    ColorDrawable d = (ColorDrawable) tiles[i][j].getBackground();
                    int color;

                    if (d.getColor() == brightColor) {
                        color = 0;
                    } else {
                        color = 1;
                    }

                    start += color;
                }
            }
        } while (checkWin() || start.equals(lastStart));

        getPreferences(MODE_PRIVATE).edit().putString("last_start", start).apply();

        // Список ходов генерации
        generationMovesText.setText(
                getString(R.string.generation_moves_title) + "\n" + moves
        );
    }

    public void reset(View v) {
        generateField();
    }

    // Победа - когда цвет каждой плитки совпадает с цветом первой
    public boolean checkWin() {
        ColorDrawable first = (ColorDrawable) tiles[0][0].getBackground();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ColorDrawable current = (ColorDrawable) tiles[i][j].getBackground();

                if (current.getColor() != first.getColor()) {
                    return false;
                }
            }
        }
        return true;
    }
}