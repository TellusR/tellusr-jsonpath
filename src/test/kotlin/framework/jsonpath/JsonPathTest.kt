package framework.jsonpath

import com.tellusr.framework.jsonpath.JsonPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JsonPathTest {
    @Test
    fun testJsonPathDocScore() {
        // Given
        val jsonElement = Json.parseToJsonElement(testJson)

        // When
        val result = JsonPath("$['docScore']").eval(jsonElement)

        // Then
        assertNotNull(result)
        assertEquals("0.5", result.toString())
    }

    @Test
    fun testJsonPathDocScorePrettyPrint() {
        val jsonEncoder = Json { prettyPrint = true }
        val jsonElement = Json.parseToJsonElement(json)
        val res = JsonPath("$['docScore']").eval(jsonElement)
        jsonEncoder.encodeToString(res).let {
            println(it)
        }

        // Then
        assertNotNull(res)
        assertEquals("0.5", res.toString())
        assert(res.jsonPrimitive.floatOrNull == 0.5f)
    }

    companion object {
        private val testJson = """
            {
                "docScore": 0.5,
                "title": "Test Document"
            }
        """.trimIndent()

        val json = """
            {
                "import_reference": "https://no.wikipedia.org/wiki/Elsa_Lystad",
                "score": 0.5,
                "thumbnail": "https://no.wikipedia.org/wiki/Elsa Lystad#/media/Fil:Lysthuset-Revyteater-AAB-102422.jpg",
                "docScore": 0.5,
                "docHits": [
                    {
                        "relevantSegments": [
                            {
                                "metadata": {
                                    "page_origin": "Elsa Lystad",
                                    "headings": [
                                        "Elsa Lystad"
                                    ]
                                },
                                "chunk_index": 0,
                                "value": "Elsa Rigmor Lystad (1930–2023) var en norsk skuespiller, kjent for sentrale roller i norsk teater, fjernsyn, film og radio. Hun fikk sitt gjennombrudd midt på 1960-tallet og var aktiv frem til 2016. Lystad var kjent for sin mimikk og sin evne til å legge inn komiske trekk på mer seriøse roller. ",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Bakgrunn",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Bakgrunn"
                                    ]
                                },
                                "chunk_index": 1,
                                "value": "Lystad ble født på Rodeløkka i Oslo. Hennes mor var vaskedame Thora Aamot (1900–1972) fra Stryn i Nordfjord, og hennes far var verksmester Erling Magnus Lystad (1905–1988). Thora Aamot skilte seg fra sin mann og flyttet hjem til Stryn i 1935. Der gikk Elsa sine to første år på skole og la til seg nordfjordsdialekt. De flyttet tilbake til Oslo i 1937.\n\nLystad gikk toårig fortsettelsesskole for piker i Oslo etter at hun hadde vært kontordame, blant annet i Dagbladets annonseavdeling.\n\nHun ble gift i 1961 med den ungarske inspisienten Arpad Szemes (1930–1998). ",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Karriere",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Karriere"
                                    ]
                                },
                                "chunk_index": 2,
                                "value": "[Fra Lysthuset Revyteater. Lystad (til høyre) sammen med fra venstre Rolv Wesenlund, Harald Heide-Steen jr., Trulte Heide Steen og Svein Byhring. ] Lystad debuterte i 1957 på Falkbergets Teater og fikk der rollen som Laura Isaksen i Bør Børson jr. og turnerte med teateret i over ett år. Hun var ansatt ved Det norske teatret i 1958-64. Gjennombruddet fikk hun i revyteatret Lysthuset, der hun ble hyret av Harald Tusberg i 1965. [1]\n\nLystad var tilknyttet Chat Noir og Fjernsynsteatret før hun ble ansatt ved Oslo Nye Teater i 1973, hvor hun var inntil hun begynte ved Den Nationale Scene i 1976.",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Karriere",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Karriere"
                                    ]
                                },
                                "chunk_index": 3,
                                "value": "Hun sluttet ved Den Nationale Scene i 1980 og jobbet deretter frilans. Hun arbeidet siden sammen med Wesenlund både på teater og i TV, og radiorollen som Fru Sløying var det første møtet med Lystad for mange nordmenn utenfor Oslo. I 1990 fikk hun rollen som Oddveig i komiserien Fredrikssons fabrikk og filmen med samme navn fra 1994. Så sent som i 2007 var hun fortsatt aktiv og turnerte med stykket Ingen grunn til panikk!, hvor hun delte hovedrollen med Marianne Krogness. Forestillingen avsluttet på Chat Noir sommeren samme år. Lystad har i en årrekke vært å høre i radioprogrammet Nitimen på NRK P1. Sammen med skuespiller kollega Morten Røhrt lagde hun det ukentlige humorinnslaget Tips-telefonen. I 2011 hadde hun rollen som Gamlemor i den norske 3D-filmen Blåfjell 2: Jakten på det magiske horn.",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Karriere",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Karriere"
                                    ]
                                },
                                "chunk_index": 4,
                                "value": "I februar 2012 spilte hun Mor Åse i en oppsetting av Peer Gynt (My name is Peer Gynt) i Kanonhallen på Løren i Oslo. Hun var aktiv frem til 2016, da hun reiste på turné sammen med Petter Anton Næss og forestillingen Mitt liv som Elsa. Hun måtte avslutte karrieren på grunn av demens. [2]\n\nElas Lystad døde 93 år gammel den 26. desember 2023; hun var katolikk og ble bisatt fra St. Hallvard katolske kirke i Oslo den 9. januar 2024. [3]\n\n[1] \n\n[2] \n\n[3] Elsa Lystad bisettes, NRK, 2023-01-09.",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Priser og utmerkelser (utvalg)",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Priser og utmerkelser (utvalg)"
                                    ]
                                },
                                "chunk_index": 5,
                                "value": "-   2014 – Amandaprisen 2014 - Juryens ærespris. -   2014 – Gullruten 2014 i klassen beste kvinnelige skuespiller for     Etter karnevalet -   2011 – Oslo bys kulturpris. Overrakt 5. mai 2011 -   2011 – Solprisen under Solfesten på Rjukan. Overrakt 9. april 2011 -   2010 – Pareliusprisen, Kunstnerforeningen -   2008 – Hedersprisen under Komiprisen 2008 -   2007 – Kongens fortjenstmedalje i gull for 50 år på scenen -   1983 – Leonardstatuetten ",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Filmografi",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Filmografi"
                                    ]
                                },
                                "chunk_index": 6,
                                "value": "  År        Tittel                                        Rolle                     Merknader   --------- --------------------------------------------- ------------------------- -----------------   2011      Blåfjell 2: Jakten på det magiske horn        Gamlemor                  Spillefilm   2011      Småbyliv                                      Fru Løfsgård              Tv-serie   2010      Den unge Fleksnes                             Sangpedagog               TV-serie   2009      God natt, elskede                             Fru Raglo                 Miniserie   2005      Slangebæreren                                 Bergljot Behrens          Miniserie   2004      Hos Martin - episoden «Hushjelp til besvær»   Gudrun Holt               TV-serie   2003      SMS - Sju magiske sirkler                     Ophelia                   TV-serie   2003      Fox Grønland - episode 11 (sesong 2)          Eldre dame                TV-serie   1997–98   Tre på toppen                                 Birgitte Berg             TV-serie   1996      Bare når jeg ler                              Dr. Torp                  TV-serie   1996      ''Maja Steinansikt                            Bestemor                  Spillefilm   1996      ''Mens vi venter på far                       Nabofruen                 TV-serie   1995      Rett i lomma                                  Kaupang                   Fjernsynsteater   1994      Seier'n er vår                                Synnøve Skuggedal         TV-serie   1994      Fredrikssons fabrikk - The movie              Oddveig                   Spillefilm   1994      Hjemme hos Paus                               Programleder              TV-serie   1991      Frida - med hjertet i hånden                  Ellinor Svendsen          Spillefilm   1991      Fedrelandet                                   Fru Horn                  Miniserie   1990      En håndfull tid                                                         Spillefilm   1990-93   Fredrikssons fabrikk                          Oddveig                   TV-serie   1989      Viva Villaveien!",
                                "field": "content_segment"
                            },
                            {
                                "metadata": {
                                    "page_origin": "Filmografi",
                                    "headings": [
                                        "Elsa Lystad",
                                        "Filmografi"
                                    ]
                                },
                                "chunk_index": 7,
                                "value": "Alice Lindeblad           Spillefilm   1988      Sweetwater                                    Marta                     Spillefilm   1986      Plastposen                                    Kioskdame                 Spillefilm   1986      Randi og Ronnis restaurant                    Enkefru Beate Hoop        TV-serie   1985      Deilig er fjorden! Rigmor                    Spillefilm   1985–88   Vill, Villere, Villaveien                     Alice Lindeblad           TV-serie   1982      Fleksnes - episoden «Rotbløyte»               Marta                     TV-serie   1981      ''Den grønne heisen                           Topsy Lorck Mathiesen     Spillefilm   1980      Belønningen                                   Ann                       Spillefilm   1980      Fabel                                         Sangerinne                Spillefilm   1975      Glade vrinsk                                                            Spillefilm   1974      Fleksnes - episoden «Biovita Helsesenter»     Oversøster Vålund         TV-serie   1974      ''Bør Børson Jr                               Frk. Finckel              Spillefilm   1973      Olsenbanden og Dynamitt-Harry går amok        Ragna, Bennys forlovede   Spillefilm   1973      Kjære lille Norge                                                       Spillefilm   1972      Fleksnes - episoden «Evig din for alltid»     Kari                      TV-serie   1972      Lukket avdeling                                                         Spillefilm   1970      Selma Brøter                                  Selma                     Fjernsynsteater   1969      22 november - den store leiegården            Syerske                   Fjernsynsteater   1968      De ukjentes marked                                                      Spillefilm   1968      Smuglere                                      Jenny                     Spillefilm   1968      Mannen som ikke kunne le                                                Spillefilm   1967      Elsk... din næste                             Frk. Andresen             Spillefilm   1967      Liv                                           Damen i vinduet           Spillefilm   1966      Hurra for Andersens                           Fru Salvesen              Spillefilm   1966      Nederlaget                                    Suzanne                   Fjernsynsteater   1966      ''Kontorsjef Tangen                                                     Fjernsynsteater   1964      Pappa tar gull                                Sykegymnasten             Spillefilm   1963      Freske fraspark                               Ollvars kone              Spillefilm   1960      Ungen                                         Sersjant-Petra            Fjernsynsteater   1958      Lån meg din kone                              Selskapsgjest             Spillefilm",
                                "field": "content_segment"
                            }
                        ],
                        "hitInformation": [
                            {
                                "score": 0.5,
                                "id": "204608_0",
                                "chunk_index": 0
                            },
                            {
                                "score": 0.49273187,
                                "id": "204608_6",
                                "chunk_index": 6
                            }
                        ]
                    }
                ],
                "author": "wikipedia",
                "id": "204608",
                "categories": [
                    "Norske komikere",
                    "Skuespillere ved Den Nationale Scene",
                    "Kongens fortjenstmedalje i gull",
                    "Vinnere av Gullruten for beste skuespiller",
                    "Norske skuespillere",
                    "Personer fra Oslo",
                    "Amandakomiteens Ærespris",
                    "Norske revyartister",
                    "Skuespillere ved Det Norske Teatret",
                    "Skuespillere ved Oslo Nye Teater"
                ],
                "title": "Elsa Lystad",
                "url": "https://no.wikipedia.org/wiki/Elsa_Lystad"
            }            
        """.trimIndent()


    }

}