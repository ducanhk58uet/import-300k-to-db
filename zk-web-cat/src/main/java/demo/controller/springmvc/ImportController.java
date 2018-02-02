package demo.controller.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by GEMVN on 2/2/2018.
 */
@Controller
@RequestMapping("/import")
public class ImportController
{
    @RequestMapping(method = RequestMethod.GET)
    public String list()
    {
        return "zul/import.zul";
    }
}
