import { stateToHTML } from "draft-js-export-html"


export default function draftToHtml(contentState, styleMap) {
    let exportHtmlStyles = {}
    console.log(styleMap)
    //reformatting style map so that it's compatible with draft-js-export-html
    for(var i = 0; i < styleMap.length; i++) {
        const child = styleMap[i]
        console.log('child: ' + JSON.stringify(child))
        const attributes = child.attributes
        console.log(attributes)
        const childName = child.name
        for(var j = 0; j < attributes.length; j++) {
            const attr = attributes[j]
            const existingAttrs = exportHtmlStyles[childName]?.style
            console.log('existing: ' + JSON.stringify(existingAttrs))
            const key = attr.attribute
            const val = attr.value
            exportHtmlStyles = {
                ...exportHtmlStyles, 
                [childName]: {
                    style: {
                        ...existingAttrs,
                        [key]: val
                    }
                }
            }
        }
    }
    console.log(exportHtmlStyles)

    let exportHtmlOptions = {
        inlineStyles: {
            ...exportHtmlStyles
        },
        //used for block styles (text aligning)
        blockStyleFn: (block) => {
            const type = block.getType()
            if(type === 'right') {
                return {
                    style: {
                        textAlign: 'right'
                    }
                }
            } else if(type === 'center') {
                return {
                    style: {
                        textAlign: 'center'
                    }
                }
            }
        }
    }

    return stateToHTML(contentState, exportHtmlOptions)
}
