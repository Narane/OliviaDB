/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metro;

/**
 *
 * @author Peter
 */
public class Metro {
    
    public static String inputBoxes(String name, String type, String value, String placeholder, String dataState) {
        
        String html = 
                "<div class='input-control " + type + "'>\n" +
                    "<input name='" + name + "' type='" + type + "' value='" + value + "' placeholder='" + placeholder + "' data-state='" + dataState + "'/>\n" + 
                "</div>\n";
        return html;
    }
    
    public static String label(String string) {
        
        String html = 
                "<label>" + string + "</label>";
        
        return html;
    }
    
    public static String submitButton() {
        
        String html = 
                "<input name='submit' type='submit' value='submit'>";
        
        return html;
    }
    
    public static String submitButton(String name, String value) {
        
        String html = 
                "<input name='" + name + "' type='submit' value='" + value + "'>";
        
        return html;
    }
    
    
    public static String textArea(String name, String value) {
        String html =   "<div class='input-control textarea' data-role='input-control'>\n" + 
                            "<textarea name='" + name + "'>" + value + "</textarea>\n" +
                        "</div>\n";
        
        return html;

    } 
                                      
    
    
}
