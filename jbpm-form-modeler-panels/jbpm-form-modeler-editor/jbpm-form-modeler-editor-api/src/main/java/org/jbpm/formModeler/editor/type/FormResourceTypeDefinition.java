package org.jbpm.formModeler.editor.type;


import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "Form Definition";
    }

    @Override
    public String getDescription() {
        return "Form Definition";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "form";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*.form";
    }
}
