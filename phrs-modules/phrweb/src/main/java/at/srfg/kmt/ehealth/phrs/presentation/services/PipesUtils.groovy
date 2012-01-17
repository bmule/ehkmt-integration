package at.srfg.kmt.ehealth.phrs.presentation.services
import groovy.json.*
class PipesUtils {

	public def getFeedTest(){

		// program to parse RSS Feed
		def rssurl = "http://feeds.digg.com/digg/news/popular.rss"
		def rssObj = new XmlSlurper().parse(rssurl)
		/*
		rssObj.channel.item.eachWithIndex { item , num ->
			println "-------------------------------------------"
			println num.toString() << " " << item.description
		}*/
	}

	public def getFeedTest2(){
		// program to parse RSS Feed and download images from digg
		//http://groovysnippets.blogspot.com/2008/11/groovy-script-to-parse-rss-with.html
		def rssurl = "http://feeds.digg.com/digg/news/popular.rss"
		def slurp = new XmlSlurper()
		def rssObj = slurp.parse(rssurl).declareNamespace(digg: "http://digg.com/docs/diggrss/", media: "http://search.yahoo.com/mrss/")
//		rssObj.channel.item.eachWithIndex { item , num ->
//			println "-------------------------------------------"
//			println item.title
//			println item."digg:category"
//			def url = item."media:thumbnail".@url.text()
//			if ( url ) {
//				println url
//				download(num, url)
//			}
//
//		}
	}

	def download(num , address)
	{
/*		def filename =  address.tokenize("/")[-2]
		def tmp = address.tokenize("/")[-1]
		def ext = tmp.tokenize(".")[-1]
		filename = filename << "." << ext
		println "saving image file : " << filename
		def file = new FileOutputStream(filename.toString())
		def out = new BufferedOutputStream(file)
		out << new URL(address).openStream()
		out.close()*/
	}
/*
	def getJsonTest(){
		def slurper = new JsonSlurper()
		def result = slurper.parseText('{"person":{"name":"Guillaume","age":33,"pets":["dog","cat"]}}')

		assert result.person.name == "Guillaume"
		assert result.person.age == 33
		assert result.person.pets.size() == 2
		assert result.person.pets[0] == "dog"
		assert result.person.pets[1] == "cat"
	}
	
	 http://mrhaki.blogspot.com/2009/08/simple-groovlet-to-read-and-display.html
	 http://groovy.codehaus.org/gapi/groovy/json/JsonSlurper.html
	 
	def getJsonTes2t(){
		import java.net.URL
		import net.sf.json.groovy.JsonSlurper

		def pipeURL = 'http://pipes.yahoo.com/pipes/pipe.run?_id=UtVVPkx83hGZ2wKUKX1_0w&_render=json'
		def pipeResult = urlFetchService.fetch(new URL(pipeURL))
		def json = new JsonSlurper().parseText(new String(pipeResult.content))

		html.html {
			head { style("""
					.item {
					border-bottom: 1px solid #eee;
					padding: 5px;
					}
					.date {
					font-style: italic;
					}
					#activities {
					border-top: 1px solid #eee;
					}
					""") }
			body {
				div(id: 'activities') {
					json.value.items.each { item ->
						div(class: 'item') {
							a(href: item.link, item.title)
							br()
							span(class: 'date', item.pubDate)
						}
					}
				}
			}
		}
	}*/
	/*
import groovy.json.*. 
def jsonText = '''
{
"message": {
"header": {
"from": "mrhaki",
"to": ["Groovy Users", "Java Users"]
},
"body": "Check out Groovy's gr8 JSON support."
}
}      
'''
 
def json = new JsonSlurper().parseText(jsonText)
 
def header = json.message.header
assert header.from == 'mrhaki'
assert header.to[0] == 'Groovy Users'
assert header.to[1] == 'Java Users'
assert json.message.body == "Check out Groovy's gr8 JSON support."
	 */
}
