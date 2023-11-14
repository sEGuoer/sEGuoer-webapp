package com.seguo.thymeleaf;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.*;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring6.context.SpringContextUtils;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

public class CustomViteElement extends AbstractElementTagProcessor {
    private static final String TAG_NAME = "vite";

    public CustomViteElement(String dialectPrefix) {
        super(TemplateMode.HTML,
                dialectPrefix,
                TAG_NAME,
                true,
                null,
                false,
                StandardDialect.PROCESSOR_PRECEDENCE
        );
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
                             IElementTagStructureHandler structureHandler) {

        IModelFactory modelFactory = context.getModelFactory();
        IModel viteModel = modelFactory.createModel();

        ApplicationContext applicationContext = SpringContextUtils.getApplicationContext(context);
        String servletContextPath = applicationContext.getEnvironment().getProperty("server.servlet.context-path", "");

        String type = tag.getAttributeValue("type");

        if ("css".equals(type)) {
            IOpenElementTag openLinkElementTag = modelFactory.createOpenElementTag("link");
            openLinkElementTag = modelFactory.setAttribute(openLinkElementTag, "rel", "stylesheet");
            openLinkElementTag = modelFactory.setAttribute(openLinkElementTag, "href", servletContextPath + new ManifestUtil().getCss());
            viteModel.add(openLinkElementTag);
            ICloseElementTag coleLinkElementTag = modelFactory.createCloseElementTag("link");
            viteModel.add(coleLinkElementTag);
        }

        if ("js".equals(type)) {
            IOpenElementTag openScriptElementTag = modelFactory.createOpenElementTag("script");
            openScriptElementTag = modelFactory.setAttribute(openScriptElementTag, "type", "module");
            openScriptElementTag = modelFactory.setAttribute(openScriptElementTag, "src", servletContextPath + new ManifestUtil().getJs());
            viteModel.add(openScriptElementTag);
            ICloseElementTag closeScriptElementTag = modelFactory.createCloseElementTag("script");
            viteModel.add(closeScriptElementTag);
        }

        structureHandler.replaceWith(viteModel, false);
    }
}
