import pdfMake from 'pdfmake/build/pdfmake'
import htmlToPdfmake from 'html-to-pdfmake'
import * as pdfFonts from 'pdfmake/build/vfs_fonts'
import draftToHtml from './DraftToHtml'

//declaring fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs
pdfMake.fonts = {
    Roboto: {
        normal: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Regular.ttf',
        bold: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Medium.ttf',
        italics: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.68/fonts/Roboto/Roboto-Italic.ttf',
        bolditalics: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-MediumItalic.ttf'
    },
    Courier: {
        normal: 'http://fontsfree.net//wp-content/fonts/basic/fixed-width/FontsFree-Net-SLC_.ttf',
        bold: 'http://allfont.es/cache/fonts/courier-bold_e82b89173be9f190daf1978d92a386c0.ttf'
    }
}

export default function draftToPdf(contentState, styleMap, title) {
    console.log(styleMap)
    const html = draftToHtml(contentState, styleMap)
    console.log(html)

    //converting html to pdfmake compatible input
    let pdfMakeInput = htmlToPdfmake(html)
    //putting it into a content object so that it's actually pdfmake compatible
    pdfMakeInput = { content: [pdfMakeInput] }

    //set default font size
    pdfMakeInput.defaultStyle = {
        fontSize: 12
    }

    //downloading
    pdfMake.createPdf(pdfMakeInput).download(title)
}
