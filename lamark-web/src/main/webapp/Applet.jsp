<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Erigir Incorporated</title>
    <meta name="keywords" content="erigir las vegas computer science research web application development"/>
    <meta name="description" content="erigir incorporated - computer sciences and research in las vegas nevada"/>
    <link href="default.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="appletDiv" style="float:none;">
    <table width="100%">
        <caption>Lamark Demo Applet</caption>
        <tr>
            <td align="center">
                <!--  I know this is bad, but cant fix right now -->
                <!--[if !IE]> Firefox and others will use outer object -->
                <object classid="java:LamarkApplet.class"
                        type="application/x-java-applet"
                        archive="download/Lamark-with-examples.jar"
                        height="640" width="1024">
                    <!-- Konqueror browser needs the following param -->
                    <param name="archive" value="download/Lamark-with-examples.jar"/>
                    <c:if test="${param.resource!=null}">
                        <param name="resource" value="${param.resource}"/>
                    </c:if>
                    <!--<![endif]-->
                    <!-- MSIE (Microsoft Internet Explorer) will use inner object -->
                    <object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"
                            codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0-windows-i586.cab"
                            height="640" width="1024">
                        <param name="code" value="LamarkApplet"/>
                        <param name="archive" value="download/Lamark-with-examples.jar"/>
                        <c:if test="${param.resource!=null}">
                            <param name="resource" value="${param.resource}"/>
                        </c:if>
                        <strong>
                            This browser does not have a Java Plug-in.
                            <br/>
                            <a href="http://java.sun.com/products/plugin/downloads/index.html">
                                Get the latest Java Plug-in here.
                            </a>
                        </strong>
                    </object>
                    <!--[if !IE]> close outer object -->
                </object>
            </td>
        </tr>
    </table>

</div>

</body>

</html>
