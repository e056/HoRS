/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.enumeration.AccessRightEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 *
 *
 */
public class SystemAdministrationModule {

    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private Employee currEmployee;
    
    
    public SystemAdministrationModule(PartnerSessionBeanRemote partnerSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, Employee currEmployee) {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.currEmployee = currEmployee;
        
    }

    public void menuSystemAdministration() throws InvalidAccessRightException {
        if (currEmployee.getAccessRightEnum() != AccessRightEnum.SYSTEM_ADMIN) {
            throw new InvalidAccessRightException("You don't have MANAGER rights to access the system administration module.");
        }

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doCreateNewEmployee();
                } else if (response == 2) {
                    doViewAllEmployeeDetails();
                } else if (response == 3) {
                    doCreateNewPartner();
                } else if (response == 4) {
                    doViewAllPartnerDetails();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }

    public void doCreateNewEmployee() {
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();

        System.out.println("*** HoRS System :: System Administration :: Create New Employee ***\n");
        System.out.print("Enter First Name> ");
        newEmployee.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newEmployee.setLastName(scanner.nextLine().trim());

        while (true) {
            System.out.print("Select Access Right (1: System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = scanner.nextInt();

            if (accessRightInt >= 1 && accessRightInt <= 4) {
                newEmployee.setAccessRightEnum(AccessRightEnum.values()[accessRightInt - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        scanner.nextLine();
        System.out.print("Enter Username> ");
        newEmployee.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newEmployee.setPassword(scanner.nextLine().trim());

        try {
            Long newStaffId = employeeSessionBeanRemote.createNewEmployee(newEmployee);
            System.out.println("New employee created successfully!: " + newStaffId + "\n");
        } catch (EmployeeUsernameExistException ex) {
            System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
        }
    }

    private void doViewAllEmployeeDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        System.out.println("*** HoRS System :: System Administration :: View All Employee Details ***\n");

        List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();

        System.out.println("------------------------");
        for (Employee employee : employees) {
            System.out.printf("%8s%20s%20s%20s%25s%20s\n", "Employee ID", "First Name", "Last Name", "Access Right", "Username", "Password");
            System.out.printf("%8s%20s%20s%20s%25s%20s\n", employee.getEmployeeId().toString(),
                    employee.getFirstName(), employee.getLastName(), employee.getAccessRightEnum().toString(),
                    employee.getUsername(), employee.getPassword());

        }

        System.out.println("------------------------");

    }

    private void doCreateNewPartner() {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS System :: System Administration :: Create New Partner ***\n");
        
        Partner newPartner = new Partner();
        System.out.print("Enter Name> ");
        newPartner.setName(scanner.nextLine().trim());
        System.out.print("Enter Username> ");
        newPartner.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        
        try {
            Long partnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
            System.out.println("New partner created successfully!: " + partnerId + "\n");
        } catch (PartnerUsernameExistException ex) {
            System.out.println("An error has occurred while creating the new partner!: The user name already exist\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
        }
        
    }

    private void doViewAllPartnerDetails() {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS System :: System Administration :: View All Partners ***\n");
        
        List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();
        
        System.out.println("------------------------");
        for (Partner partner : partners) {
            System.out.printf("%8s%20s%25s%20s\n", "Partner ID", "Name", "Username", "Password");
            System.out.printf("%8s%20s%25s%20s\n", partner.getPartnerId().toString(),
                    partner.getName(), partner.getUsername(), partner.getPassword());

        }
        System.out.println("------------------------");
     
    }
}

