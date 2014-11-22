package ece356;

/**
 *
 * @author Heesang
 * peter get back to work
 */
public class MarkupHelper {
    
    public static String HeadOpen(String pageTitle, String role){
        StringBuilder sb = new StringBuilder(256);
        sb.append("<!DOCTYPE html><html><head><title>");
        sb.append(pageTitle);
        sb.append("</title><link rel=\"stylesheet\" href=\"styles.css\"></head><body><div class=\"wrapper\">");
        sb.append(buildSidebar(role));
        sb.append("<section>");
        sb.append("<span class=\"octicon octicon-link\"></span><h2>");
        sb.append(pageTitle);
        sb.append("</h2>");
        return sb.toString();
    }
    
    public static String HeadClose(){
        StringBuilder sb = new StringBuilder(32);
        sb.append("</section></div></body></html>");
        
        return sb.toString();
    }
    
    public static String buildSidebar(String role){
        StringBuilder sb = new StringBuilder(256);
        sb.append("<sidebar>");
        
        
        sb.append("TESTBAR");
        
        
        sb.append("</sidebar>");
        return sb.toString();
    }
}
