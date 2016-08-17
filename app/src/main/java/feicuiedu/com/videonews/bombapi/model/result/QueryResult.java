package feicuiedu.com.videonews.bombapi.model.result;

import java.util.List;

/**
 * 查询数据的结果，常对应{@link retrofit2.http.GET}方法
 */
public class QueryResult<T> {

    @SuppressWarnings("unused")
    private List<T> results;

    public List<T> getResults() {
        return results;
    }
}
