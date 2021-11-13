/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ANGELY
 */
@Remote
public interface EmployeeSessionBeanRemote {
    
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;

    public List<Employee> retrieveAllEmployees();

    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public List<Employee> viewAllEmployee();

    public Long createNewEmployee(Employee newEmployee) throws EmployeeUsernameExistException, UnknownPersistenceException,InputDataValidationException;
    
    
}
