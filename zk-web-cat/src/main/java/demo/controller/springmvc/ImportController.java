package demo.controller.springmvc;

import io.yoobi.ApplyETL;
import io.yoobi.FileManager;
import io.yoobi.YoobiTest;
import io.yoobi.model.DataResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;

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
