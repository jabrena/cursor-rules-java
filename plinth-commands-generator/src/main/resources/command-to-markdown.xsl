<?xml version="1.0" encoding="UTF-8"?>
<!--
  Command XML → Cursor slash-command Markdown.
  Mirrors agent-to-markdown.xsl: narrative contract lives in goal CDATA;
  structured elements follow commands.xsd order:
  metadata → goal → constraints → steps → output-format → safeguards
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                exclude-result-prefixes="xsl">

  <xsl:output method="text" encoding="UTF-8" indent="no"/>
  <xsl:strip-space elements="command constraints constraint-list steps output-format output-format-list safeguards safeguards-list"/>

  <xsl:template match="/command">
    <xsl:text>---
description: </xsl:text>
    <xsl:call-template name="yaml-single-quoted-scalar">
      <xsl:with-param name="text" select="metadata/description"/>
    </xsl:call-template>
    <xsl:text>
argument-hint: </xsl:text>
    <xsl:call-template name="yaml-single-quoted-scalar">
      <xsl:with-param name="text" select="metadata/argument-hint"/>
    </xsl:call-template>
    <xsl:text>
model: </xsl:text>
    <xsl:call-template name="yaml-single-quoted-scalar">
      <xsl:with-param name="text" select="metadata/model"/>
    </xsl:call-template>
    <xsl:text>
agent: </xsl:text>
    <xsl:call-template name="yaml-single-quoted-scalar">
      <xsl:with-param name="text" select="metadata/agent"/>
    </xsl:call-template>
    <xsl:text>
tools:
</xsl:text>
    <xsl:for-each select="metadata/tools/tools-list/tool">
      <xsl:text>  - </xsl:text>
      <xsl:call-template name="yaml-single-quoted-scalar">
        <xsl:with-param name="text" select="."/>
      </xsl:call-template>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>metadata:</xsl:text>
    <xsl:if test="metadata/authors/author[normalize-space(.) != '']">
      <xsl:variable name="authors">
        <xsl:call-template name="authors"/>
      </xsl:variable>
      <xsl:text>
  author: </xsl:text>
      <xsl:call-template name="yaml-single-quoted-scalar">
        <xsl:with-param name="text" select="$authors"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:text>
  version: </xsl:text>
    <xsl:call-template name="yaml-single-quoted-scalar">
      <xsl:with-param name="text" select="metadata/version"/>
    </xsl:call-template>
    <xsl:text>
</xsl:text>
    <xsl:text>---

</xsl:text>
    <xsl:text># </xsl:text>
    <xsl:value-of select="@id"/>
    <xsl:text>

</xsl:text>
    <xsl:apply-templates select="goal"/>
    <xsl:apply-templates select="constraints"/>
    <xsl:apply-templates select="steps"/>
    <xsl:apply-templates select="output-format"/>
    <xsl:apply-templates select="safeguards"/>
  </xsl:template>

  <xsl:template name="authors">
    <xsl:for-each select="metadata/authors/author[normalize-space(.) != '']">
      <xsl:if test="position() &gt; 1">
        <xsl:text>, </xsl:text>
      </xsl:if>
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="yaml-single-quoted-scalar">
    <xsl:param name="text"/>
    <xsl:text>'</xsl:text>
    <xsl:call-template name="yaml-single-quoted-content">
      <xsl:with-param name="text" select="$text"/>
    </xsl:call-template>
    <xsl:text>'</xsl:text>
  </xsl:template>

  <xsl:template name="yaml-single-quoted-content">
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="contains($text, &quot;'&quot;)">
        <xsl:value-of select="substring-before($text, &quot;'&quot;)"/>
        <xsl:text>''</xsl:text>
        <xsl:call-template name="yaml-single-quoted-content">
          <xsl:with-param name="text" select="substring-after($text, &quot;'&quot;)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Goal already contains Markdown headings; emit trimmed body with no ## Goal wrapper -->
  <xsl:template match="goal">
    <xsl:call-template name="trim">
      <xsl:with-param name="text" select="."/>
    </xsl:call-template>
    <xsl:text>

</xsl:text>
  </xsl:template>

  <xsl:template match="constraints">
    <xsl:text>## </xsl:text>
    <xsl:choose>
      <xsl:when test="normalize-space(constraints-description) != ''">
        <xsl:value-of select="normalize-space(constraints-description)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>Constraints</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>

</xsl:text>
    <xsl:for-each select="constraint-list/constraint">
      <xsl:text>- </xsl:text>
      <xsl:call-template name="trim">
        <xsl:with-param name="text" select="."/>
      </xsl:call-template>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="steps">
    <xsl:text>## </xsl:text>
    <xsl:choose>
      <xsl:when test="normalize-space(@heading) != ''">
        <xsl:value-of select="normalize-space(@heading)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>Workflow</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>

</xsl:text>
    <xsl:for-each select="step">
      <xsl:choose>
        <xsl:when test="@number and normalize-space(@number) != ''">
          <xsl:value-of select="normalize-space(@number)"/>
          <xsl:text>. </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="position()"/>
          <xsl:text>. </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="trim">
        <xsl:with-param name="text" select="."/>
      </xsl:call-template>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="output-format">
    <xsl:text>## </xsl:text>
    <xsl:choose>
      <xsl:when test="normalize-space(@heading) != ''">
        <xsl:value-of select="normalize-space(@heading)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>Output</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>

</xsl:text>
    <xsl:for-each select="output-format-list/output-format-item | item">
      <xsl:text>- </xsl:text>
      <xsl:call-template name="trim">
        <xsl:with-param name="text" select="."/>
      </xsl:call-template>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="safeguards">
    <xsl:if test="safeguards-list/safeguards-item[normalize-space(.) != '']">
      <xsl:text>## Safeguards

</xsl:text>
      <xsl:for-each select="safeguards-list/safeguards-item[normalize-space(.) != '']">
        <xsl:text>- </xsl:text>
        <xsl:call-template name="trim">
          <xsl:with-param name="text" select="."/>
        </xsl:call-template>
        <xsl:text>
</xsl:text>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="trim">
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="$text = ''"/>
      <xsl:when test="starts-with($text, ' ') or starts-with($text, '&#x9;') or starts-with($text, '&#xA;') or starts-with($text, '&#xD;')">
        <xsl:call-template name="trim">
          <xsl:with-param name="text" select="substring($text, 2)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="trim-trailing">
          <xsl:with-param name="text" select="$text"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="trim-trailing">
    <xsl:param name="text"/>
    <xsl:variable name="last" select="substring($text, string-length($text), 1)"/>
    <xsl:choose>
      <xsl:when test="$text = ''"/>
      <xsl:when test="$last = ' ' or $last = '&#x9;' or $last = '&#xA;' or $last = '&#xD;'">
        <xsl:call-template name="trim-trailing">
          <xsl:with-param name="text" select="substring($text, 1, string-length($text) - 1)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
