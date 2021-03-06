package com.mx.finerio.controller.imp

import com.mx.finerio.controller.DownloadController
import com.mx.finerio.dtos.Arguments
import com.mx.finerio.dtos.Podcast
import com.mx.finerio.enums.Quality
import com.mx.finerio.services.DownloadService
import com.mx.finerio.services.HtmlHandlerService
import com.mx.finerio.services.imp.DownloadServiceImp
import com.mx.finerio.services.imp.HtmlHandlerServiceImp
import org.apache.log4j.Logger

import java.util.stream.Stream

class DownloadControllerImp implements DownloadController {

    public static final String DEFAULT_CATEGORY = 'p086934c/episodes/downloads'

    private static String BBC_PODCAST_URL(String category) {"https://www.bbc.co.uk/programmes/${category}"}

    private static Logger log = Logger.getLogger(DownloadControllerImp.class)

    HtmlHandlerService handlerService = new HtmlHandlerServiceImp()
    DownloadService downloadService = new DownloadServiceImp()

    @Override
    void downloadPodcasts(String[] args){
        Arguments arguments = buildArguments(args)
        List<Podcast> podcasts = handlerService.podcastFromHtml(
                BBC_PODCAST_URL(arguments.categoryToken ? arguments.categoryToken : DEFAULT_CATEGORY)
        )
        if(podcasts.isEmpty()){
            log.error("No podcast found on page")
            return
        }
        isQualityArgsFilterDownload(arguments) ? filteredDownload(podcasts, arguments) : fullQualityDownload(podcasts)
    }

    private Stream<Podcast> fullQualityDownload(List<Podcast> podcasts) {
        podcasts.stream().parallel().each { downloadService.tryDownload(it) }
    }

    private static boolean isQualityArgsFilterDownload(Arguments arguments) {
        arguments.quality == Quality.HIGHER.value || arguments.quality == Quality.LOWER.value
    }

    private Stream<Podcast> filteredDownload(List<Podcast> podcasts, arguments) {
        podcasts.stream()
                .filter({ podcast -> podcast.quality == arguments.quality })
                .parallel().each { downloadService.tryDownload(it) }
    }

    private static Arguments buildArguments(String[] args) {
        Arguments arguments = new Arguments()
        if (args.size() > 0) {
            arguments.with {
                categoryToken = args.first()
                quality = args.last()
            }
        }
        arguments
    }
}
