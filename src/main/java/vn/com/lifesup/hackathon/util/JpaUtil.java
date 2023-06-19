package vn.com.lifesup.hackathon.util;

import vn.com.lifesup.hackathon.dto.base.BaseDTO;
import javax.persistence.Query;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@UtilityClass
public class JpaUtil {

    public void setQueryParams(Query query, Map<String, Object> params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return;
        }

        params.forEach(query::setParameter);
    }

    public void setQueryParams(Query query, Query queryCount, Map<String, Object> params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return;
        }

        params.forEach((k, v) -> {
            query.setParameter(k, v);
            queryCount.setParameter(k, v);
        });
    }

    public <T extends BaseDTO> void setPaging(T obj, Query query, Query queryCount) {
        if (Objects.isNull(obj.getPage())) {
            obj.setPage(1);
        }

        if (Objects.isNull(obj.getPageSize())) {
            obj.setPageSize(10);
        }
        query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
        query.setMaxResults(obj.getPageSize().intValue());

        obj.setTotalRow(((BigDecimal) queryCount.getSingleResult()).longValue());
        long totalPage = (obj.getTotalRow() % obj.getPageSize()) == 0 ? obj.getTotalRow() / obj.getPageSize()
                : obj.getTotalRow() / obj.getPageSize() + 1;
        obj.setTotalPage(totalPage);
    }

    public <T> PageImpl<T> getPageableResult(Query query, Query queryCount, HashMap<String, Object> params, Pageable pageable) {
        JpaUtil.setQueryParams(query, queryCount, params);
        query.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        long count = ((BigDecimal) queryCount.getSingleResult()).longValue();
        return new PageImpl<>(query.getResultList(), pageable, count);
    }

    public String toCountQuery(String sql) {
        return "SELECT count(*) from (" + sql + ")";
    }

    public String toQueryParam(String param) {
        return Constants.SPECIAL_CHAR.COLON + param;
    }
}
