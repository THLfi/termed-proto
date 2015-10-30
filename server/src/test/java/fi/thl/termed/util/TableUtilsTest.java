package fi.thl.termed.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TableUtilsTest {

    @Test
    public void shouldBeAbleToTransformRowToMap() {
        List<String[]> rows = Lists.newArrayList();
        rows.add(new String[]{"name", "age"});
        rows.add(new String[]{"John", "25"});

        Map<String, String> map = Maps.newHashMap();
        map.put("name", "John");
        map.put("age", "25");
        List<Map<String, String>> maps = Lists.newArrayList();
        maps.add(map);

        Assert.assertEquals(maps, TableUtils.toMapped(rows));
    }

    @Test
    public void shouldBeAbleToTransformMapToRow() {
        Map<String, String> john = Maps.newLinkedHashMap();
        john.put("name", "John");
        john.put("age", "25");
        Map<String, String> jack = Maps.newLinkedHashMap();
        jack.put("name", "Jack");
        jack.put("age", "35");
        Map<String, String> lisa = Maps.newLinkedHashMap();
        lisa.put("name", "Lisa");
        lisa.put("age", "45");

        List<Map<String, String>> maps = Lists.newArrayList();
        maps.add(john);
        maps.add(jack);
        maps.add(lisa);

        List<String[]> rows = TableUtils.toTable(maps);

        Assert.assertArrayEquals(new String[]{"name", "age"}, rows.get(0));
        Assert.assertArrayEquals(new String[]{"John", "25"}, rows.get(1));
        Assert.assertArrayEquals(new String[]{"Jack", "35"}, rows.get(2));
        Assert.assertArrayEquals(new String[]{"Lisa", "45"}, rows.get(3));
    }

}
