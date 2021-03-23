package com.tomilin.fitnessTracker;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class LoginHandler {

    Path path;
    Serializer serializer;

    public LoginHandler(String path) {
        try {
            this.path = Paths.get(path);
            serializer = new Serializer(path);
        } catch (InvalidPathException | JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяем файл на существование. Если файл уже был, тогда
     * вызываем метод, для поиска введенного логина. Если файла не было,
     * вызываем метод регистрации нового пользователя.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String login = scanner.nextLine();
        try {
            if (!Files.exists(this.path)) {
                System.out.println("!!You are first user the app!!");
                registerUser(login, true);
            } else {
                findUser(login);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Регистрация нового пользователя.
     * @param login Имя нового пользователя.
     * @param isNewFile true - если файла с данными не было, то создаем новый.
     *                  false - если файл уже существует.
     */
    private void registerUser(String login,Boolean isNewFile) {
        System.out.println("<<Register a new user>>");
        // Создаем пользователя и заполняем свойства.
        User newUser = new User();
        Scanner sc = new Scanner(System.in);
        System.out.print("Input name: ");
        var name = sc.nextLine();
        System.out.print("Input surname: ");
        var surname = sc.nextLine();

        if (name.equals("")) {
            System.out.println("Invalid name, exit.");
            System.exit(1);
        }
        if (surname.equals("")) {
            System.out.println("Invalid surname, exit.");
            System.exit(1);
        }
        newUser.setLogin(login);
        newUser.setName(name);
        newUser.setSurname(surname);
        try {
            Users updatedUsers = new Users();
            if (isNewFile) {
                updatedUsers.setUsers(Arrays.asList(newUser));
                Files.createFile(path.toAbsolutePath());
            } else {
                /* Выполняем десериализацию текущего XML и
                добавляем нового юзера. */
                var users = serializer.deserialize();
                users.add(newUser);
                updatedUsers.setUsers(users);
            }
            // Выполняем сериализацию обновленного списка.
            serializer.serialize(updatedUsers);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(login + " created!");
    }

    /**
     * Если пользователь найден, выводим информацию. И начинаем тренировку.
     * Если не найлен, вызываем метод регистрации.
     * @param login Имя пользователя.
     */
    private void findUser(String login) {
        try {
            // Десериализуем файл, читаем всех пользователей.
            var users = serializer.deserialize();
            var foundUser = users.parallelStream()
                    .filter(i->i.getLogin().equals(login)).findFirst();
            if (foundUser.isPresent()) {
                Double totalCalories = foundUser.get().getTotalCalories();
                var userInfo = String.format("\nHello %s!\n" +
                                "Total burned: %.2f calories\n" +
                                "Last session: %.2f calories\n",
                        foundUser.get().getName(),
                        foundUser.get().getTotalCalories(),
                        foundUser.get().getLastSession());
                System.out.println(userInfo);
                Training training = new Training();
                training.startTraining();
                System.out.println("Press any key to stop training.");
                System.in.read();
                training.stopTraining();
                Users toSerialize = new Users();
                // Обновляем информацию.
                users.parallelStream()
                        .filter(i->i.getLogin().equals(login))
                        .forEach(i->
                        {
                            i.setTotalCalories(totalCalories +
                                    training.getWastedCalories());
                            i.setLastSession(training.getWastedCalories());
                        });
                // Обновляем список и выполняем сериализацию.
                toSerialize.setUsers(users);
                serializer.serialize(toSerialize);
            } else {
                System.out.println("Login " + login + " not found!");
                registerUser(login, false);
            }
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }
}
