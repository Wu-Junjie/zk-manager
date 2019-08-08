package org.felix.study.zkmanager;

import org.felix.study.zkmanager.utils.XmlUtil;
import org.junit.Test;

public class XmlTest {

    @Test
    public void test() throws Exception {
        XmlUtil xmlUtil = new XmlUtil("nodes.xml");

        //xmlUtil.add("host","localhost222");

        System.out.println(xmlUtil.getAllMap());

    }
}
