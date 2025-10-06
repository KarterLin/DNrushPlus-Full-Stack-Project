package com.dnrush.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dnrush.entity.ImageResource;
import com.dnrush.entity.NavigationItem;
import com.dnrush.entity.Statistic;
import com.dnrush.entity.TeamMember;
import com.dnrush.service.ImageService;
import com.dnrush.service.NavigationService;
import com.dnrush.service.SiteContentService;
import com.dnrush.service.StatisticService;
import com.dnrush.service.TeamMemberService;

@Controller
@RequestMapping("/")
public class HomeController {
    
    @Autowired
    private NavigationService navigationService;
    
    @Autowired
    private SiteContentService siteContentService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private TeamMemberService teamMemberService;
    
    @Autowired
    private StatisticService statisticService;
    
    @GetMapping
    public String index(Model model) {
        // 導航欄資料
        List<NavigationItem> navigationItems = navigationService.getActiveRootItems();
        model.addAttribute("navigationItems", navigationItems);
        
        // Hero區塊內容
        String heroTitle = siteContentService.getContentValue("hero_title", "DiviNe Kartrider Team.");
        String heroSubtitle = siteContentService.getContentValue("hero_subtitle", "DiviNe車隊前身為競速組oDriFTo與道具組oNigHTo，成立於2007年1月3日，為跑跑卡丁車歷史最悠久的車隊之一。");
        model.addAttribute("heroTitle", heroTitle);
        model.addAttribute("heroSubtitle", heroSubtitle);
        
        // Hero背景圖片
        List<ImageResource> heroImages = imageService.getImagesByCategory("hero");
        if (!heroImages.isEmpty()) {
            model.addAttribute("heroBackgroundImage", heroImages.get(0).getBase64Data());
        }
        
        // About區塊內容
        String aboutTitle = siteContentService.getContentValue("about_title", "車隊介紹");
        String aboutContent1 = siteContentService.getContentValue("about_content_1", "DiviNe車隊前身為競速組 oDriFTo 與道具組 oNigHTo ，成立於2007年1月3日，為跑跑卡丁車史上歷史最悠久的車隊之一。");
        String aboutContent2 = siteContentService.getContentValue("about_content_2", "2007年8月，為避免不同番號下的DN競速組與道具組所造成對外之不同解讀，統一更名為DiviNe，從此之後不再區分競速道具，為現今DiviNe車隊之由來，簡稱為DN車隊。");
        String aboutContent3 = siteContentService.getContentValue("about_content_3", "跑跑卡丁車Rush+於2020年5月12日正式上線，同時DiviNe車隊由原班人馬於同日正式創立Rush+車隊分部。");
        
        model.addAttribute("aboutTitle", aboutTitle);
        model.addAttribute("aboutContent1", aboutContent1);
        model.addAttribute("aboutContent2", aboutContent2);
        model.addAttribute("aboutContent3", aboutContent3);
        
        // About區塊圖片
        List<ImageResource> aboutImages = imageService.getImagesByCategory("about");
        model.addAttribute("aboutImages", aboutImages);
        
        // 統計數據
        List<Statistic> statistics = statisticService.getActiveStatistics();
        model.addAttribute("statistics", statistics);
        
        // Services區塊內容
        String servicesTitle = siteContentService.getContentValue("services_title", "團隊取得的佳績");
        String servicesSubtitle = siteContentService.getContentValue("services_subtitle", "隊史館");
        model.addAttribute("servicesTitle", servicesTitle);
        model.addAttribute("servicesSubtitle", servicesSubtitle);
        
        // Services區塊圖片
        List<ImageResource> serviceImages = imageService.getImagesByCategory("service");
        model.addAttribute("serviceImages", serviceImages);
        
        // Portfolio區塊內容
        String portfolioTitle = siteContentService.getContentValue("portfolio_title", "團隊情誼不僅僅只是線上");
        String portfolioSubtitle = siteContentService.getContentValue("portfolio_subtitle", "線下隊聚活動");
        model.addAttribute("portfolioTitle", portfolioTitle);
        model.addAttribute("portfolioSubtitle", portfolioSubtitle);
        
        // 隊聚活動照片 (從 ImageResource 中獲取 event 分類的照片)
        List<ImageResource> eventImages = imageService.getImagesByCategory("event");
        model.addAttribute("eventImages", eventImages);
        
        // 獲取可用的年份列表（從數據庫的 year 欄位，排除 null 值）
        List<Integer> activeYearsInt = imageService.getDistinctYears();
        List<String> activeYears = activeYearsInt.stream()
            .filter(year -> year != null) // 過濾掉 null 值
            .map(String::valueOf)
            .sorted((a, b) -> b.compareTo(a)) // 降序排列
            .toList();
        model.addAttribute("activeYears", activeYears);
        
        // Team區塊內容
        String teamTitle = siteContentService.getContentValue("team_title", "Team");
        String teamSubtitle = siteContentService.getContentValue("team_subtitle", "CHECK OUR TEAM");
        model.addAttribute("teamTitle", teamTitle);
        model.addAttribute("teamSubtitle", teamSubtitle);
        
        // 團隊成員
        List<TeamMember> teamMembers = teamMemberService.getActiveMembers();
        model.addAttribute("teamMembers", teamMembers);
        
        // Rules區塊內容
        String rulesTitle = siteContentService.getContentValue("rules_title", "填寫入隊申請前請先詳閱");
        String rulesSubtitle = siteContentService.getContentValue("rules_subtitle", "隊規、入隊標準");
        model.addAttribute("rulesTitle", rulesTitle);
        model.addAttribute("rulesSubtitle", rulesSubtitle);
        
        // Rules區塊圖片
        List<ImageResource> rulesImages = imageService.getImagesByCategory("rules");
        model.addAttribute("rulesImages", rulesImages);
        
        // Contact區塊內容
        String contactTitle = siteContentService.getContentValue("contact_title", "請留下您的聯繫方式");
        String contactSubtitle = siteContentService.getContentValue("contact_subtitle", "入隊申請");
        model.addAttribute("contactTitle", contactTitle);
        model.addAttribute("contactSubtitle", contactSubtitle);
        
        return "index";
    }
    
    @GetMapping("/rules")
    public String rules(Model model) {
        // 導航欄資料
        List<NavigationItem> navigationItems = navigationService.getActiveRootItems();
        model.addAttribute("navigationItems", navigationItems);
        
        // Rules區塊內容
        String rulesTitle = siteContentService.getContentValue("rules_title", "填寫入隊申請前請先詳閱");
        String rulesSubtitle = siteContentService.getContentValue("rules_subtitle", "隊規、入隊標準");
        model.addAttribute("rulesTitle", rulesTitle);
        model.addAttribute("rulesSubtitle", rulesSubtitle);
        
        // Rules區塊圖片
        List<ImageResource> rulesImages = imageService.getImagesByCategory("rules");
        model.addAttribute("rulesImages", rulesImages);
        
        return "rules";
    }
    
    @GetMapping("/service-details")
    public String serviceDetails(Model model) {
        // 導航欄資料
        List<NavigationItem> navigationItems = navigationService.getActiveRootItems();
        model.addAttribute("navigationItems", navigationItems);
        
        // Services區塊內容
        String servicesTitle = siteContentService.getContentValue("services_title", "團隊取得的佳績");
        String servicesSubtitle = siteContentService.getContentValue("services_subtitle", "隊史館");
        model.addAttribute("servicesTitle", servicesTitle);
        model.addAttribute("servicesSubtitle", servicesSubtitle);
        
        // Services區塊圖片
        List<ImageResource> serviceImages = imageService.getImagesByCategory("service");
        model.addAttribute("serviceImages", serviceImages);
        
        return "service-details";
    }
}
