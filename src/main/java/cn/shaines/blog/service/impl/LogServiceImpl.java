package cn.shaines.blog.service.impl;

import cn.shaines.blog.entity.Log;
import cn.shaines.blog.repository.LogRepository;
import cn.shaines.blog.service.LogService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description: Log service接口实现
 * @date: created in 2019-05-02 14:52:46
 * @author: houyu
 */
@Service
public class LogServiceImpl extends BaseServiceImpl<Log, String, LogRepository> implements LogService {

    @Override
    public Map<String, Integer> getWeekAccess() {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, -6);// 减6天
        String startDateStr = new SimpleDateFormat("yyyy-MM-dd").format(startCalendar.getTime());
        String endDateStr = new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime());
        Map<String, Integer> accessMap = this.findAccessByDateBetween(startDateStr, endDateStr);
        // 避免中间没有记录, 前端无显示
        for (int i = 0; i < 7; i++) {
            String thisDateStr = new SimpleDateFormat("MM/dd").format(startCalendar.getTime());
            if (!accessMap.containsKey(thisDateStr)) {
                accessMap.put(thisDateStr, 0);
            }
            startCalendar.add(Calendar.DATE, 1);
        }
        return accessMap;
    }

    @Override
    public int deleteWeekBefore() {
        Calendar calendar = Calendar.getInstance();
        // 删除七天前的数据
        calendar.add(Calendar.DATE, -7);
        String endData = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        int i = this.baseRepository.deleteToEndData(endData);
        return i;
    }

    private Map<String, Integer> findAccessByDateBetween(String startDateStr, String endDateStr){
        List<Map<String, Object>> mapList = this.baseRepository.findAccessByDateBetween(startDateStr, endDateStr);
        Map<String, Integer> dataMap = new TreeMap<>();
        for (Map<String, Object> map : mapList) {
            dataMap.put(map.get("date") + "", Integer.valueOf(map.get("sum") + ""));
        }
        return dataMap;
    }
}
