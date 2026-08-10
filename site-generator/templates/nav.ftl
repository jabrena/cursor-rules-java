<#include "jbake-config-text.ftl">
<#if (config.author_gtm)?has_content>
<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=${config.author_gtm}}"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
</#if>
<nav class="navbar navbar-default navbar-fixed-top navbar-custom">
<div class="container-fluid">

<div class="navbar-header">
<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#main-navbar">
  <span class="sr-only">Toggle navigation</span>
  <span class="icon-bar"></span>
  <span class="icon-bar"></span>
  <span class="icon-bar"></span>
</button>
<a class="navbar-brand" href="${content.rootpath!}index.html">${site_title_string}</a>
</div>

<div class="collapse navbar-collapse" id="main-navbar">
<ul class="nav navbar-nav navbar-right">
  <li><a href="https://forms.gle/TpNXENjmu45wuXoi6" target="_blank">Share feedback</a></li>
  <li class="navlinks-container">
    <a class="navlinks-parent" href="javascript:void(0)">Conferences</a>
    <div class="navlinks-children">
      <a href="https://jabrena.github.io/plinth/jcconf-2026/index.html" target="_blank">JCConf 2026</a>
      <a href="https://jabrena.github.io/plinth/codemotion-madrid-2026/index.html" target="_blank">Codemotion Madrid 2026</a>
      <a href="https://jabrena.github.io/plinth/dvbe25/index.html" target="_blank">Devoxx BE 2025</a>
      <a href="https://jabrena.github.io/101-cursor/" target="_blank">WJAX-2025</a>
    </div>
  </li>
  <li class="navlinks-container">
    <a class="navlinks-parent" href="javascript:void(0)">Courses</a>
    <div class="navlinks-children">
      <a href="${content.rootpath!}courses/system-prompts-java/index.html">System prompts for Java</a>
      <a href="${content.rootpath!}courses/java-generics/index.html">Java Generics</a>
      <a href="${content.rootpath!}courses/profile-memory-leak/index.html">Memory Leak Profiling</a>
    </div>
  </li>
  <li><a href="${content.rootpath!}archive.html">Archive</a></li>
  <li><a href="https://jabrena-github-io.translate.goog/cursor-rules-java/?_x_tr_sl=en&_x_tr_tl=cn&_x_tr_hl=en-US&_x_tr_pto=wapp">Translate</a></li>
</ul>
</div>

<#if (content.image)??>
<div class="avatar-container">
  <div class="avatar-img-border">
    <a href="${content.rootpath!}">
      <img class="avatar-img" src="$!{content.rootpath}${content.image}"/>
  </a>
  </div>
</div>
<#elseif (config.site_avatar)??>

</#if>

</div>
</nav>
