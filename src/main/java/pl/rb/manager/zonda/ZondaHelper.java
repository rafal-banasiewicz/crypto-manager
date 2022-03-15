package pl.rb.manager.zonda;

import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

class ZondaHelper {

    private static List<String> getStringDates() {
        List<String> dates = new ArrayList<>();
        for (int i = 2016; i < 2031; i++) {
            dates.add(String.valueOf(i));
        }
        return dates;
    }

    static void loadCommonZondaAttributes(Model model) {
        model.addAttribute("spent", null);
        model.addAttribute("dates", getStringDates());
    }

}
