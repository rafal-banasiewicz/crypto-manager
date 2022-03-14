package pl.rb.manager.utils;

import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class CommonHelper {

    public static List<String> getStringDates() {
        List<String> dates = new ArrayList<>();
        for (int i = 2016; i < 2031; i++) {
            dates.add(String.valueOf(i));
        }
        return dates;
    }

    public static void loadCommonZondaAttributes(Model model) {
        model.addAttribute("spent", null);
        model.addAttribute("dates", getStringDates());
    }

}
