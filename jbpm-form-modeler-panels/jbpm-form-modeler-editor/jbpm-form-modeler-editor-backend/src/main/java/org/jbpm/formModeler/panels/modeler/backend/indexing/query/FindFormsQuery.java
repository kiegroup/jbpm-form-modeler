/*
 * Copyright 2014 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.panels.modeler.backend.indexing.query;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.FormIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.BasicQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.QueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class FindFormsQuery implements NamedQuery {

    @Inject
    private DefaultResponseBuilder responseBuilder;

    @Override
    public String getName() {
        return "FindFormsQuery";
    }

    @Override
    public Set<IndexTerm> getTerms() {
        return new HashSet<IndexTerm>() {{
            add(new FormIndexTerm());
        }};
    }

    @Override
    public Query toQuery(Set<ValueIndexTerm> terms, boolean useWildcards) {
        PortablePreconditions.checkNotNull("terms", terms);
        if (terms.size() != 1) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + FormIndexTerm.TERM + "'." );
        }
        final Map<String, ValueIndexTerm> normalizedTerms = normalizeTerms(terms);
        final ValueIndexTerm ruleTerm = normalizedTerms.get(FormIndexTerm.TERM);
        if ( ruleTerm == null ) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + FormIndexTerm.TERM + "'." );
        }

        final QueryBuilder builder = new BasicQueryBuilder();
        if ( useWildcards ) {
            builder.useWildcards();
        }
        builder.addTerm( ruleTerm );
        return builder.build();
    }

    private Map<String, ValueIndexTerm> normalizeTerms( final Set<ValueIndexTerm> terms ) {
        final Map<String, ValueIndexTerm> normalizedTerms = new HashMap<String, ValueIndexTerm>();
        for ( ValueIndexTerm term : terms ) {
            normalizedTerms.put( term.getTerm(),
                    term );
        }
        return normalizedTerms;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }
}
