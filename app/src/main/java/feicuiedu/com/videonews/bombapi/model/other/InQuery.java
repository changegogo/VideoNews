package feicuiedu.com.videonews.bombapi.model.other;

import feicuiedu.com.videonews.bombapi.BombConst;

/**
 * <pre>
 * 'where= { 字段名称: {
 *              "$inQuery": {
 *                  "where": {
 *                     "objectId": 对象Id
 *                  },
 *                  "className": 对象表名
 *              }
 *            }
 *         }'
 * </pre>
 */
public class InQuery {

    private final String field;

    private final String objectId;

    private final String className;


    public InQuery(String field, String objectId, String className){
        this.field = field;
        this.objectId = objectId;
        this.className = className;
    }

    @Override public String toString() {
        return String.format(BombConst.LIKES_IN_QUERY, field, objectId, className);
    }
}
