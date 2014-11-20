package UserTypes;

/**
 *
 * @author Heesang
 * get back to work
 */
public  class UserFinder {
    
    public static BaseUser GetUser(int index){
        if(index == 0){
            return new BaseUser();
        }
        else if(index == 1){
            return new UserDoctor();
        }
        return null;
    }
}
