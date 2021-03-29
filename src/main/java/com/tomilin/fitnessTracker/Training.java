package com.tomilin.fitnessTracker;

import com.google.common.base.Stopwatch;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Scanner;

public class Training {

    // Calories per hour
    private final EnumMap<TypeOfTraining, Double> caloriesTraining = new
            EnumMap<>(TypeOfTraining.class) {
                {
                    put(TypeOfTraining.JUMP_ROPE, 800.0);
                }

                {
                    put(TypeOfTraining.PUSH_UPS, 550.55);
                }

                {
                    put(TypeOfTraining.SIT_UP, 1100.0);
                }
            };

    private Stopwatch stopwatch;
    private Duration elapsedTime;
    private TypeOfTraining selectedTraining;
    private Double wastedCalories;

    public Double getWastedCalories() {
        return  wastedCalories;
    }

    public void startTraining() {
        showTrainings();
        selectTraining();
        startTimer();
    }

    public void stopTraining() {
        stopTimer();
        calculateCalories();
        showAndUpdateInfo();
    }

    /**
     * Показываем данные за текущую тренировку и обновляем общие калории.
     */
    private void showAndUpdateInfo() {
        String str;
        if (elapsedTime.toMinutes() == 0) {
            str = " < one min";
        } else {
            str = elapsedTime.toMinutes() + " min";
        }
        String infoString = String.format("\nInfo:\n" +
                        "Training duration: %s\n" +
                        "Selected training: %s\n" +
                        "Burned calories: %.2f",
                str, selectedTraining, wastedCalories);
        System.out.println(infoString);
    }

    private void showTrainings() {
        System.out.println("Accessible types of training:");
        caloriesTraining.forEach((key, value) -> {
            var fStr = String.format("%s - %.2f cal", key, value);
            System.out.println(fStr);
        });
    }

    private void selectTraining() {
        System.out.println("\nSelect one of them: ");
        while (true) {
            System.out.print("Input a type: ");
            try {
                selectedTraining = TypeOfTraining
                        .valueOf(new Scanner(System.in).nextLine());
                caloriesTraining.get(selectedTraining);
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("\nInvalid type! Try again.");
            }
        }
    }

    private void startTimer() {
        System.out.println("!!Start timer!!");
        stopwatch = Stopwatch.createStarted();
    }

    private void stopTimer() {
        System.out.println("!!End timer!!");
        elapsedTime = this.stopwatch.elapsed();
        System.out.println("Elapsed time: " + elapsedTime.toSeconds() + " sec");
    }

    /**
     * calories = k * t
     * k - calories per hour
     * t - seconds
     */
    private void calculateCalories() {
        Double k = caloriesTraining.get(selectedTraining);
        long t = elapsedTime.getSeconds();
        short secondsInHour = 3600;
        wastedCalories = (k * t) / secondsInHour;
    }
}
