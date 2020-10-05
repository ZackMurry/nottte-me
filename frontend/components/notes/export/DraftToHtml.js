import { stateToHTML } from 'draft-js-export-html'

export default function draftToHtml(contentState, styleMap) {
    let exportHtmlStyles = {}
    console.log(styleMap)
    //reformatting style map so that it's compatible with draft-js-export-html
    for (let i = 0; i < styleMap.length; i++) {
        const child = styleMap[i]
        console.log('child: ' + JSON.stringify(child))
        const { attributes } = child
        console.log(attributes)
        const childName = child.name
        for (let j = 0; j < attributes.length; j++) {
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

    const exportHtmlOptions = {
        inlineStyles: {
            ...exportHtmlStyles
        },
        //used for block styles (text aligning)
        blockStyleFn: block => {
            const type = block.getType()
            if (type === 'right') {
                return {
                    style: {
                        textAlign: 'right'
                    }
                }
            }
            if (type === 'center') {
                return {
                    style: {
                        textAlign: 'center'
                    }
                }
            }
            return {}
        }
    }

    return stateToHTML(contentState, exportHtmlOptions)
}
