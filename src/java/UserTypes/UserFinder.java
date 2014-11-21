package UserTypes;

/**
 *
 * @author Heesang
 * get back to work
 */
public  class UserFinder {
    
    public static BaseUser GetUser(String role){
        if(role.equals("doctor")){
            return new UserDoctor();
        }
        else if(role.equals("finance")){
            return new UserFinance();
        }
        else if(role.equals("legal")){
            return new UserLegal();
        }
        else if(role.equals("patient")){
            return new UserPatient();
        }
        else if(role.equals("staff")){
            return new UserStaff();
        }
 
        return null;
    }
}
