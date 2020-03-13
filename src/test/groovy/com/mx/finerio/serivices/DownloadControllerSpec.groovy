package com.mx.finerio.serivices

import com.mx.finerio.dtos.Podcast
import com.mx.finerio.controller.DownloadController
import com.mx.finerio.services.DownloadService
import com.mx.finerio.services.HtmlHandlerService
import com.mx.finerio.controller.imp.DownloadControllerImp
import com.mx.finerio.utils.TestUtils
import org.jsoup.nodes.Element
import spock.lang.Specification

class DownloadControllerSpec extends Specification{

    DownloadController downloadController = new DownloadControllerImp()

    def setup(){
        downloadController.handlerService = Mock(HtmlHandlerService)
        downloadController.downloadService = Mock(DownloadService)
    }

    def "Should download  "() {
        given:'a Element instance'
        Element element = new TestUtils().buildHtmlDocument().getElementsByTag('a').first()

        and:'a podcast instance'
        Podcast podcast = new Podcast(element)
        Podcast podcast1 = new Podcast(element)

       when:
       downloadController.downloadPodcasts()

       then:
       1 * downloadController.handlerService.podcastFromHtml() >> [podcast, podcast1]
       2 * downloadController.downloadService.tryDownload(_ as Podcast)

    }

    def "Should not download  on empty"() {
        when:
        downloadController.downloadPodcasts()

        then:
        1 * downloadController.handlerService.podcastFromHtml() >> []
        0 * downloadController.downloadService.tryDownload(_ as Podcast)

    }


}
