/** layuiAdmin.std-v2020.4.1 LPPL License By https://www.layui.com/admin/ */
;layui.extend({setter: "config", admin: "lib/admin", view: "lib/view"}).define(["setter", "admin"], function (a) {
    var e = layui.setter, i = layui.element, n = layui.admin, t = n.tabsPage, d = layui.view, l = function (a, d) {
        var l, u = r("#LAY_app_tabsheader>li"), b = a.replace(/(^http(s*):)|(\?[\s\S]*$)/g, "");
        if (u.each(function (e) {
            var i = r(this), n = i.attr("lay-id");
            n === a && (l = !0, t.index = e)
        }), d = d || "新标签页", e.pageTabs) l || (setTimeout(function () {
            r(s).append(['<div class="layadmin-tabsbody-item layui-show">', '<iframe src="' + a + '" frameborder="0" class="layadmin-iframe"></iframe>', "</div>"].join(""))
        }, 10), t.index = u.length, i.tabAdd(o, {title: "<span>" + d + "</span>", id: a, attr: b})); else {
            var y = n.tabsBody(n.tabsPage.index).find(".layadmin-iframe");
            y[0].contentWindow.location.href = a
        }
        i.tabChange(o, a), n.tabsBodyChange(t.index, {url: a, text: d})
    }, s = "#LAY_app_body", o = "layadmin-layout-tabs", r = layui.$;
    r(window);
    n.screen() < 2 && n.sideFlexible(), layui.config({base: e.base + "modules/"}), layui.each(e.extend, function (a, i) {
        var n = {};
        n[i] = "{/}" + e.base + "lib/extend/" + i, layui.extend(n)
    }), d().autoRender(), layui.use("common"), a("index", {openTabsPage: l})
});