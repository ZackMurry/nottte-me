import { stateToHTML } from "draft-js-export-html"
import pdfMake from 'pdfmake/build/pdfmake'
import htmlToPdfmake from 'html-to-pdfmake'
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
import draftToHtml from "./draftToHtml";

//declaring fonts
pdfMake.vfs = pdfFonts.pdfMake.vfs
pdfMake.fonts = {
    Roboto: {
        normal: 'https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Regular.ttf'
    },
    Courier: {
        normal: 'http://fontsfree.net//wp-content/fonts/basic/fixed-width/FontsFree-Net-SLC_.ttf'
    }
}

export default function draftToPdf(contentState, styleMap, title, doc) {
    let html = draftToHtml(contentState, styleMap)
    
    //converting html to pdfmake compatible input
    var pdfMakeInput = htmlToPdfmake(html)
    //putting it into a content object so that it's actually pdfmake compatible
    pdfMakeInput = {content: [pdfMakeInput]}
    
    //optional converting to Courier so that google docs can read it better (maybe add option to disable)
    //google docs has trouble reading ligatures in Courier, but a main goal of this is to be
    //compatible with docs
    if(doc) {
        pdfMakeInput.defaultStyle = {
            font: 'Courier',
            fontSize: 12
        }
    } else {
        //set default font size
        pdfMakeInput.defaultStyle = {
            fontSize: 12
        }
    }
    
    //downloading
    pdfMake.createPdf(pdfMakeInput).download(title)
    
}
