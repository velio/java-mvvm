package net.velyo.mvvm;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Created by velyo.ivanov on 5/15/2015.
 */
public abstract class AbstractModel implements Model {

    // Fields
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    protected ModelState state;
    @Inject @FormBinder
    protected Instance<ModelBinder> formBinder;
    @Inject @MultipartBinder
    protected Instance<ModelBinder> multipartBinder;
    @Inject
    protected Instance<HtmlHelper> helperInstance;

    // Methods
    // ////////////////////////////////////////////////////////////////////////////////////////////

    @java.lang.Override
    public ModelState getModelState() {
        return this.state;
    }

    @java.lang.Override
    public Class<?> getModelType() {
        return this.getClass();
    }

    @java.lang.Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        this.bind(request);
        helperInstance.get().setModel(this);

        if("POST".equalsIgnoreCase(request.getMethod())) {
            boolean valid;

            if (IValidatable.class.isAssignableFrom(this.getClass()))
                ((IValidatable) this).validate(state);

            if (valid = state.isValid())
                valid = this.save(request);

            if(valid) {
                if(this.isPostRedirected()) {
                    String url = getPostRedirect();
                    if(url == null)
                        url = request.getRequestURI();
                    response.sendRedirect(url);
                }
                return;
            }
        }

        this.load(request);
    }

    protected void bind(HttpServletRequest request) throws ServletException {

        try {
            String contentType = request.getContentType();
            boolean isMultipart = (contentType != null) &&
                    (contentType.toLowerCase().indexOf("multipart/form-data") > -1);
            if(!isMultipart)
                formBinder.get().bind(request, this);
            else
                multipartBinder.get().bind(request, this);
        }
        catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    protected void load(HttpServletRequest request) {
        // TODO add functionality on override
    }

    protected boolean save(HttpServletRequest request) {
        // TODO add functionality on override
        return true;
    }

    protected String getPostRedirect(){
        return null;
    }

    protected boolean isPostRedirected(){
        return true;
    }
}
