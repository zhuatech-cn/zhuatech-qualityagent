/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.qualityagent.workbench;

import static cn.zhuatech.qualityagent.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "categories");
    Set<String> ids = new HashSet<>();
    var categories = new ArrayList<>(rows(r, "categories", 1, 200));
    long total = 0;
    for (var c : categories) {
      keys(c, "id", "count");
      unique(ids, id(c, "id"));
      total += integer(c, "count", 0, 1000000000);
    }
    categories.sort(
        Comparator.<Map<String, Object>>comparingInt(c -> ((Number) c.get("count")).intValue())
            .reversed()
            .thenComparing(c -> c.get("id").toString()));
    long cumulative = 0;
    List<Map<String, Object>> out = new ArrayList<>();
    for (var c : categories) {
      long n = ((Number) c.get("count")).longValue();
      boolean priority =
          n > 0
              && BigDecimal.valueOf(cumulative)
                      .compareTo(BigDecimal.valueOf(total).multiply(new BigDecimal("0.8")))
                  < 0;
      cumulative += n;
      out.add(
          Map.of(
              "id",
              c.get("id"),
              "count",
              n,
              "sharePercent",
              total == 0 ? BigDecimal.ZERO : round(n * 100.0 / total),
              "cumulativePercent",
              total == 0 ? BigDecimal.ZERO : round(cumulative * 100.0 / total),
              "priority",
              priority));
    }
    return Map.of("totalDefects", total, "rateDefined", total > 0, "categories", out);
  }
}
