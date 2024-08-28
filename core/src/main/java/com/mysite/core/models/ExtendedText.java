package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = { Text.class },
        resourceType = "apps/mysite/components/myCustomComponent/customText",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ExtendedText implements Text {

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String text;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean isRichText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fd;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fn;


    @Override
    public String getText() {
        if (text != null && !isRichText) {
            return StringUtils.strip(text);
        } else {
            return text;
        }
    }

    @Override
    public boolean isRichText() {
        return isRichText;
    }

    @Override
    public String getId() {
        return null;
    }

    public String getFd() {
        return fd;
    }

    public String getFn() {
            return fn;
    }
}
