
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sebastian
 */
@FacesConverter("idConverter")
public class IdConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
       return string;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o.toString();
    }
    
}
