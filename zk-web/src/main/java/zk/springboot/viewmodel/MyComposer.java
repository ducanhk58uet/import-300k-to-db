package zk.springboot.viewmodel;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zss.ui.Spreadsheet;

/**
 * Created by GEMVN on 1/19/2018.
 */
public class MyComposer extends SelectorComposer<Component> {

    @Wire
    Spreadsheet ss;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        //initialize stuff here
        //ss.setSrc("/web/startzss.xlsx");
    }
}
