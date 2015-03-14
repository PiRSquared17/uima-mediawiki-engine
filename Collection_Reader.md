

# Introduction #
This page will present the Collection Reader component that is part of this project. This component is intended to be used within the [Apache UIMA](http://uima.apache.org/) framework. In just a few words, it takes XML dumps from various wikipedia-like projects, extracts the pages and turns them into a suitable format to be processed into the UIMA framework.

# How to use it #
To do

# Technical stuff #
In this section I will talk a bit more about the technical aspects of this component.

## Input ##
In this section I will put together a few things that one should know about the files we take as an input.

The files we are working on are database dumps of the projects managed by the Wikimedia foundation. These files are made freely available [here](http://dumps.wikimedia.org/) under the _Database backup dumps_ section. They are provided sometimes in raw **XML** format but more often under the form of **7Zip**, **BZip2** or **GZip** archives. Our component can **GZip** and **BZip2** files as well as raw XML natively (the component handled the unpacking for you). **7Zip** however is **not supported**. These files can typically weigh from 50 MB for smaller projects up to several GB for the bigger ones.

### Format ###
These XML files are supposed to validate a XSD schema that can be found [here](http://www.mediawiki.org/xml/export-0.4.xsd). You can also find some more documentation about it [there](http://meta.wikimedia.org/wiki/Help:Export). To save you the hassle, hereafter is a slightly improved extract of the second link that shows what such a document shall contain with all the optional tags :

```
<mediawiki xml:lang="en">
    <siteinfo>
       <sitename>My wiki website</sitename>
       <base>http://mywebsite.org/wiki/home</base>
       <generator>Generator name</generator>
       <case>first-letter</case>
       <namespaces>
          <namespace key="-2" case="first-letter">Media</namespace>
          <namespace key="-1" case="first-letter">Spécial</namespace>
          <namespace key="0" case="first-letter" />
          <namespace key="1" case="first-letter">Talk</namespace>
          ...
       </namespaces>
   </siteinfo>
   <page>
       <title>Page title</title>
       <id>1337</id>
       <redirect />
       <restrictions>edit=sysop:move=sysop</restrictions>
       <revision>
          <id>1234</id>
          <timestamp>2001-01-15T13:15:00Z</timestamp>
          <contributor>
             <username>Foobar</username>
             <id>11111</id>
             <ip>127.0.0.1</ip>
          </contributor>
          <minor />
          <comment>I have just one thing to say!</comment>
          <text>A bunch of text here.</text>
       </revision>
       <revision>
          <timestamp>2001-01-15T13:10:27Z</timestamp>
          <contributor>
             <ip>10.0.0.2</ip>
          </contributor>
          <comment>new!</comment>
          <text>An earlier revision.</text>
       </revision>
       ...
   </page>
   <page>
       <title>Talk:Page title</title>
       <revision>
          <timestamp>2001-01-15T14:03:00Z</timestamp>
          <contributor><ip>10.0.0.2</ip></contributor>
          <comment>hey</comment>
          <text>WHYD YOU LOCK PAGE??!!! i was editing that jerk</text>
      </revision>
      <upload>
         ...
      </upload>
      <logitem>
         ...
      </logitem>
   </page>
   ...
</mediawiki>
```

However, most of these tags are optional. The following contains only the mandatory tags (`<siteinfo>`, `<page>` and `<revision>` tags are not even mandatory, but without them the document is pretty much empty)  :
```
<mediawiki xml:lang="en">
    <siteinfo>
       <sitename>My wiki website</sitename>
       <base>http://mywebsite.org/wiki/home</base>
       <generator>Generator name</generator>
       <case>first-letter</case>
       <namespaces>
          <namespace key="-2" case="first-letter">Media</namespace>
          <namespace key="-1" case="first-letter">Spécial</namespace>
          <namespace key="0" case="first-letter" />
          <namespace key="1" case="first-letter">Talk</namespace>
          ...
       </namespaces>
   </siteinfo>
   <page>
       <title>Page title</title>
       <revision>
          <timestamp>2001-01-15T13:15:00Z</timestamp>
          <contributor />
          <text>A bunch of text here.</text>
       </revision>
       ...
   </page>
   ...
</mediawiki>
```

The documents passed on to this component should obviously respect this formatting. If you take them from the Wikimedia foundation website, all is well. If you provide your own wiki stuff, you might want to validate it against the XSD schema first.

### Extracting information ###
The Collection Reader component is based on the Stax API for the XML parsing. Events get pulled out of the stream, and are identified by their type and name.

We chose to consider only a subset of tags stored in an Enum :
```
enum MWTag {
		// ROOT
		MEDIAWIKI,
		// SITE INFO
		SITEINFO, SITENAME, BASE, GENERATOR, CASE, NAMESPACES, NAMESPACE,
		// PAGES
		PAGE, TITLE, ID,
		// REVISIONS
		REVISION, TIMESTAMP, CONTRIBUTOR, USERNAME, MINOR, COMMENT, TEXT,
		// INVALID, WILL BE SKIPPED
		INVALID_TAG;
}
```

We use the tag's name to convert it to the corresponding constant. All the tags that are unreferenced point to one single special constant INVALID\_TAG. This allows us to skip those tags whenever we encounter them.

In the table below you can see the tags and their (expected) corresponding content :

| **Tag name** | **Description** | **Example** |
|:-------------|:----------------|:------------|
| 

&lt;mediawiki&gt;

 | The document's root tag | _N/A_ |
| 

&lt;siteinfo&gt;

 | The siteinfo's root tag | _N/A_ |
| 

&lt;sitename&gt;

 | The website's (Wiki project) name | 

&lt;sitename&gt;

Wikinews

&lt;/sitename&gt;

 |
| 

&lt;base&gt;

 | The website's base URL | 

&lt;base&gt;

http://fr.wikinews.org/wiki/Accueil

&lt;/base&gt;

 |
| 

&lt;generator&gt;

 | The name of the tool used to create the dump | 

&lt;generator&gt;

MediaWiki 1.16wmf4

&lt;/generator&gt;

 |
| 

&lt;namespaces&gt;

 | The root item for the namespaces | _N/A_ |
| 

&lt;namespace&gt;

 | The description of a namespace | 

&lt;namespace key="1" case="first-letter"&gt;

Discussion

&lt;/namespace&gt;

 |
| 

&lt;page&gt;

 | The root item for pages | _N/A_ |
| 

&lt;title&gt;

 | The page's title | 

&lt;title&gt;

 A title 

&lt;/title&gt;

 |
| 

&lt;id&gt;

 | The page's or revision's id (depending on nesting) | 

&lt;id&gt;

1337

&lt;/id&gt;

 |
| 

&lt;revision&gt;

 | The root item for revisions | _N/A\_ _|
| 

&lt;timestamp&gt;

 | The revision's timestamp |  

&lt;timestamp&gt;

2010-12-25T12:12:12Z

&lt;/timestamp&gt;

  |
| 

&lt;username&gt;

 | The contributor's username | 

&lt;username&gt;

 Some name

&lt;/username&gt;

 |
| 

&lt;minor&gt;

 | Indicates if the revision is minor or not | 

&lt;minor /&gt;

 |
| 

&lt;comment&gt;

 | The revision's comment | 

&lt;comment&gt;

 Some comment 

&lt;/comment&gt;

 |
| 

&lt;text&gt;

 | The revision's text | 

&lt;text&gt;

 A whole lot of text with Wiki syntax 

&lt;/text&gt;

 |


### Relevant information ###

For our treatment, not all information is relevant. We consider the following type system :