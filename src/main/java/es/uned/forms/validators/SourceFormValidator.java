package es.uned.forms.validators;

import es.uned.forms.SourceForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 */
@Component
public class SourceFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return SourceForm.class.equals(aClass);
    }

    @Override
    public void validate(Object form, Errors errors) {
        SourceForm sourceForm = (SourceForm) form;
        if (!sourceForm.getSource().equals("Dataset"))
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "term", "NotEmpty");
        else if (sourceForm.getFile() != null && sourceForm.getFile().isEmpty()) {
            errors.rejectValue("file", "NotEmpty");
        }
    }
}
