package com.tomilin.fitnessTracker;

public class App
{
    private static final String PATH_TO_XML = "./data/users.xml";
    public static void main(String[] args)
    {
        System.out.print("Welcome to \"FitnessTrackerApp\"!" +
                "\nInput your login: ");
        LoginHandler loginHandler = new LoginHandler(PATH_TO_XML);
        loginHandler.run();
    }
}
