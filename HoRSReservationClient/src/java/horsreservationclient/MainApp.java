/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import entity.Guest;
import java.util.Scanner;
import util.exception.GuestPassportNumExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
public class MainApp {

    private RoomReservationSessionBeanRemote roomReservationSessionBean;

    private GuestSessionBeanRemote guestSessionBeanRemote;
    private Guest currGuest;

    public MainApp(RoomReservationSessionBeanRemote roomReservationSessionBean, GuestSessionBeanRemote guestSessionBeanRemote) {
        this.roomReservationSessionBean = roomReservationSessionBean;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Point-of-Sale (POS) System (v4.1) ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        menuMain();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterGuest();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String passport = "";
        String password = "";

        System.out.println("*** HoRS Reservation System :: Login ***\n");
        System.out.print("Enter Passport Number> ");
        passport = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        if (passport.length() > 0 && password.length() > 0) {
            System.out.println("Logging in...");
            this.currGuest = guestSessionBeanRemote.guestLogin(passport, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    public void doRegisterGuest() {
        Scanner scanner = new Scanner(System.in);
        Guest newGuest = new Guest();

        System.out.println("*** HoRS Reservation Client :: Register As Guest ***\n");
        System.out.print("Enter First Name> ");
        newGuest.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newGuest.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Passport Number> ");
        newGuest.setPassportNumber(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newGuest.setPassword(scanner.nextLine().trim());

        try {
            Long newGuestId = guestSessionBeanRemote.createNewGuest(newGuest);
            System.out.println("Successfully registered! Please log in with your credentials. ");
        } catch (GuestPassportNumExistException ex) {
            System.out.println("An error has occurred while creating the new guest: The passport number already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new guest: " + ex.getMessage() + "\n");
        }
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are login as " + currGuest.getFullName());
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: Front Office");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {

                } else if (response == 2) {

                } else if (response == 3) {

                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

}
