package com.mysite.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = "mysite/components/articleComp"
)
@Exporter(name = "jackson", extensions = "json")
public class ArticleDialogModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleDialogModel.class);

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String description;

    @ValueMapValue
    @Named("cq:tags")
    private String[] tags;

    @SlingObject
    private ResourceResolver resolver;

    @Inject
    private Resource resource;

    private List<TagDetails> tagDetailsList;

    @PostConstruct
    protected void init() {
        tagDetailsList = new ArrayList<>();

        LOGGER.info("Tags to be resolved: {}", Arrays.toString(tags));
        TagManager tagManager = resolver.adaptTo(TagManager.class);

        if (Objects.nonNull(tags) && Objects.nonNull(tagManager)) {
            for (String tagId : tags) {
                Tag tag = tagManager.resolve(tagId);
                if (tag != null) {
                    TagDetails tagDetails = new TagDetails(tag);
                    tagDetailsList.add(tagDetails);
                    LOGGER.info("Resolved tag: title={}, description={}, resourceType={}",
                            tagDetails.getTitle(), tagDetails.getDescription(), tagDetails.getResourceType());
                } else {
                    LOGGER.warn("Could not resolve tag ID: {}", tagId);
                }
            }
        } else {
            LOGGER.warn("Tags or TagManager is null");
        }

        if (tagDetailsList.isEmpty()) {
            tagDetailsList = Collections.emptyList();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String[] getTags() {
        return tags;
    }

    public List<TagDetails> getTagDetails() {
        return tagDetailsList;
    }

    public static class TagDetails {
        private static final Logger LOGGER = LoggerFactory.getLogger(TagDetails.class);

        private final String title;
        private final String description;
        private final String resourceType;

        public TagDetails(Tag tag) {
            this.title = tag.getTitle();
            this.description = tag.getDescription();
            this.resourceType = tag.getPath();
        }

        public String getTitle() {
            LOGGER.info("Tag title: {}", title);
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getResourceType() {
            return resourceType;
        }
    }
}
