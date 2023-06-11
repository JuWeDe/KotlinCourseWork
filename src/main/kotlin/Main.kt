import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
suspend fun main(args: Array<String>) {
    val urls = listOf(
        "https://upload.wikimedia.org/wikipedia/commons/d/db/Nissan_Silvia_Varietta_%28S15%29_front.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/1/1b/SILVIA_CSP311_001.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/4/40/Nissan_New_Silvia.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/2d/1991NissanSilva.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/b3/71_Datsun_240Z_%287765523298%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/c/c3/71_Datsun_240Z_%287765521520%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/0/01/1979_Datsun_280ZX_%28S130%29_hatchback_%2825478423000%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/d6/1989_Nissan_300ZX_Automatic_pic4.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/9/95/1984_Nissan_300ZX_%28Z31%29_50th_Anniversary_hatchback_%282015-08-07%29_02.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/29/Z33.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/69/370z12.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/75/Nissan_FAIRLADY_Z_%28Z34%29_Version_ST%2C_2022%2C_left-front.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/84/94-95_Mercedes-Benz_E-Class_sedan.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/d8/Mercedes_C124_front_20080424.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/9a/Mercedes_A124_rear.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/2/26/500e2a.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/c/cc/E63.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/9b/2019_Mercedes-Benz_E220d_SE_Automatic_2.0_Front.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/7a/2018_Mercedes-Benz_E_300_%28W_213%29_sedan_%282018-11-02%29_03.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/7b/2017-03-07_Geneva_Motor_Show_1105.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/b/b8/2017_Mercedes-Benz_E220d_AMG_Line_Premium%2B_2.0_Rear.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/1/14/BMW_528_%28E12%29_%E2%80%93_Heckansicht%2C_22._August_2013%2C_M%C3%BCnster.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/82/1983_BMW_528i_%28E28%29_sedan_%2821539857803%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/5/53/BMW_525i_E34.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/e/ed/1989_BMW_525i_%28E34%29_sedan_%282015-11-13%29_02.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/b2/2000-2003_BMW_525i_%28E39%29_Executive_sedan_%282010-10-02%29_01.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/f/f2/2000-2003_BMW_525i_%28E39%29_Executive_sedan_%282011-11-17%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/5/53/BMW_530i_%28E60%29_Facelift_20090615_front.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/a/a9/2007-2010_BMW_520d_%28E60%29_sedan_02.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/e/e8/BMW5er_6.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/6f/2010_BMW_528i_%28F10%29_sedan_01.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/db/BMW_G30_530e_IMG_3862.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/ae/W201_1.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/df/Mercedes_W201_front_20080108.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/95/Mercedes_Benz_190_E_black_rear.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/b4/DTM_Meisterauto_1992.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/8c/Mercedes_W202_front_20080302.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/0/07/W202_back.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/a/ac/Mathias_Lauda_2006_DTM.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a3/Mercedes-Benz_W204.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/3/38/Ralf_Schumacher_Brands_Hatch_DTM_2008.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a3/Mercedes-Benz_C_220_BlueTEC_AMG_Line_%28W_205%29_%E2%80%93_Frontansicht%2C_15._M%C3%A4rz_2014%2C_D%C3%BCsseldorf.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/83/Mercedes-Benz_C_220_BlueTEC_Avantgarde_%28W_205%29_%E2%80%93_Heckansicht%2C_20._Juni_2014%2C_D%C3%BCsseldorf.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/21/2017_Mercedes-Benz_C250_AMG_Line_Premium%2B_2.1.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/24/Mercedes-Benz_W206_IMG_5796.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a8/Mercedes-Benz_C-Class_All-Terrain_IAA_2021_1X7A0279.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/d0/Mercedes-AMG_C_63_S_-_Mondial_de_l%27Automobile_de_Paris_2014_-_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a3/BMW_E21_front_20080331.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/8b/BMW_E21_323i_Baur.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/4/4a/BMW_E30_front_20080127.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/86/BMW_324td_Touring_E30_%288149152230%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/66/BMW-E36-sedan.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/be/BMW_318ti_1997.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/69/BMW320i_E46_Lim.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/bf/BMW_M3_%284570377139%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/e/e2/BMW_E90_front_20090301.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/4e/BMW_3-Series_converible_DC.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/f/f0/BMW_316i_%28F30%29_registered_May_2013_1598cc_02.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/2/29/2018_BMW_330e_M_Sport_Shadow_Edition_2.0_Front.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/45/BMW_G20%2C_Paris_Motor_Show_2018%2C_IMG_0492.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/4e/BMW_G21_IMG_2023.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/8d/FoS20162016_0624_104957AA_%2827886438175%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/23/BMW_M3_GTR_%2827934687222%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/0/01/Skyline_C10.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/b3/Nissan_Skyline_C111_2000_GTX-E_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/90/Nissan_Skyline_C211_2000_GT-EL_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/1/1a/DR30-01.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/25/Nissan_Skyline_R31_2000_GTS-R_002.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/1/15/Nissan_Skyline_R32_front.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/42/Nissan_Skyline_R32_GT-R_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/7d/Nissan_Skyline_R33_GT-R_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/3/38/Nissan_Skyline_R34_a.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/73/Nissan_Skyline_R34_GT-R_N%C3%BCr_001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/2/26/V35.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/e/e2/Nissan_SKYLINE_400R_%285BA-RV37%29_front.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/6a/NISSAN_GT-R_CONCEPT_at_TMS2001_003.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/1/1d/Nissan_GT-R_PROTO.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/8f/Nissan_Skyline_GT-R_V35.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/c/cc/Nissan_GTR_Nismo_01.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/4e/1979_Toyota_Celica_Supra_Mk_I_Coupe_%2823155184553%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/f/f7/Perf_pack_for_wiki.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/f/fd/Toyota-CelicaXX2800GT.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/4/42/Cgb_1984_Toyota_Supra_MT5.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/4f/1988_Toyota_Supra_3.0i_%286391348751%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/6b/Black_1997_Toyota_Supra_Limited_Edition_6_Speed_Twin_Turbo_with_Targa_Top.png",
        "https://upload.wikimedia.org/wikipedia/commons/4/49/%28Flickr%29_A_car-03.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/42/20_Toyota_Supra.png",
        "https://upload.wikimedia.org/wikipedia/commons/e/e2/Porsche_Carrera_4S_front_20080519.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/3/3e/Mitsubishi_Lancer_Evolution_GSR_III_%28Orange_Julep%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/83/Mitsubishi_Lancer_Evolution_II_%28CE9A%29_in_Thailand_07.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/4/4e/95_mitsubishi_lancer_Evolution_lll_gsr.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/3/34/David%27s_Mitsubishi_Lancer_Evolution_IV.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/d/d7/Lancer_Evo_V.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/9a/Mitsubishi-Lancer-Evolution_r%C3%B6d.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/9/93/2015_Rally_Bohemia_-_Schumann%2C_Mitsubishi_Lancer_Evolution.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/a/a0/Mitsubishi_Lancer_Evolution_X_01.JPG",
        "https://upload.wikimedia.org/wikipedia/commons/e/ef/1981_Mazda_RX-7_%28FB_Series_2%29_coupe_%2815979998413%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a8/1986_Mazda_RX-7_%28FC%29_coupe_%2821105743820%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a6/Mazda-rx7-3rd-generation01.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/b/b8/Mazda_RX-8_on_freeway.jpg"

    )


    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
        expectSuccess = true
    }

//    1st method
    runBlocking {
        val deferredImages = urls.map { url ->
            async {

                client.get(url).body<ByteArray>()
            }
        }
        val images = deferredImages.awaitAll()
        images.forEachIndexed { index, image ->
            File("ImagesWithNewNames/image${index + 1}.jpg").writeBytes(image)
        }
    }
//    2nd method nonfunctional
    val jobs = mutableListOf<Job>()

    urls.forEach { url ->
        jobs.add(GlobalScope.launch {
            val imageData = client.get(url).body<ByteArray>()
            val fileName = url.substringAfterLast('/')
            val path = "ImagesWithOldNames/$fileName"
            File(path).writeBytes(imageData)
        })
    }

    jobs.forEach { it.join() }
    client.close()
//    3rd method functional

    runBlocking {
        urls.map {
            async {
                val imageData = client.get(it).body<ByteArray>()
                val fileName = it.substringAfterLast('/')
                File("ImagesWithOldNamesFunc/$fileName.jpg").writeBytes(imageData)
            }
        }.awaitAll()
    }
    val sum = operateOnNumbers(10, 5) { x, y -> x + y }
    val multiply = operateOnNumbers(5, 2) { x, y -> x * y }
    val difference = operateOnNumbers(50, 30) { x, y -> x - y }

    println("Sum: $sum")
    println("Multiply: $multiply")
    println("Difference: $difference")


    val rus = greet("ru")
    println(rus())

    val esp = greet("es")
    println(esp())

    val eng = greet("en")
    println(eng())

    val fra = greet("fr")
    println(fra())

    val ita = greet("ita")
    println(ita())

}

fun greet(language: String): () -> String {
    return when (language) {
        "en" -> {
            { "Hello" }
        }

        "fr" -> {
            { "Bonjour" }
        }

        "es" -> {
            { "Hola" }
        }

        "ru" -> {
            { "Привет" }
        }

        else -> {
            { "Unsupported language" }
        }

    }
}

fun operateOnNumbers(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}
