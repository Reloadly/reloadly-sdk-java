package software.reloadly.sdk.core.internal.filter;


import software.reloadly.sdk.core.internal.util.Asserter;


public class QueryFilter extends BaseFilter {

    /**
     * Filter by page
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize   the amount of items per page to retrieve.
     * @return this filter instance
     */
    public QueryFilter withPage(int pageNumber, int pageSize) {
        Asserter.assertNotNull(pageNumber, "Page number");
        Asserter.assertNotNull(pageSize, "Page size");

        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Filter page number must be greater than zero");
        }

        if (pageSize <= 0) {
            throw new IllegalArgumentException("Filter page size must be greater than zero");
        }

        parameters.put("page", pageNumber);
        parameters.put("size", pageSize);
        return this;
    }
}
