package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>
 * The {@link CompositeFilter} allows to group several servlet {@link Filter}
 * into one. This is used to allow customizable filters and provide a backwards
 * compatible {@link RequestLoggingFilter}.
 * </p>
 * 
 * <p>
 * You can easily create a subclass of {@link CompositeFilter} and add all the
 * filters from com.sap.hcp.cf.logging.servlet.filter you wish. You can even
 * bring your own filters.
 * </p>
 */
public class CompositeFilter implements Filter {

    private List<? extends Filter> filters;

    public CompositeFilter(Filter... filters) {
        this.filters = Arrays.asList(filters);
    }

    public void setFilters(List<? extends Filter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    public List<? extends Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        for (Filter filter: this.filters) {
            filter.init(config);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                              ServletException {
        new InternalFilterChain(chain, this.filters).doFilter(request, response);
    }

    @Override
    public void destroy() {
        for (int i = this.filters.size() - 1; i >= 0; i--) {
            Filter filter = this.filters.get(i);
            filter.destroy();
        }
    }

    private static class InternalFilterChain implements FilterChain {

        private final FilterChain originalChain;
        private final Iterator<? extends Filter> current;

        public InternalFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
            this.originalChain = chain;
            this.current = additionalFilters.iterator();
        }

        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException,
                                                                                           ServletException {
            if (current.hasNext()) {
                current.next().doFilter(request, response, this);
            } else {
                originalChain.doFilter(request, response);
            }
        }
    }

}
