import Head from "next/head"
import { useRouter } from "next/router"
import Navbar from "../../components/Navbar"
import Cookie from 'js-cookie'
import { Typography } from "@material-ui/core"
import { useEffect, useState } from "react"

export default function UseLinkShare() {
    const router = useRouter()
    const { shareId } = router.query

    const [ jwt, setJwt ] = useState(Cookie.get('jwt')) //todo change jwts to this to preserve state

    const [ noteIdentifier, setNoteIdentifier ] = useState({})

    useEffect(() => {
        if(!jwt) {
            router.push(`/login?redirect=/l/${shareId}`)
        } else if(shareId) {
            getNoteIdentifier()
        }
    }, [ shareId ])

    const getAccessToNote = async () => {
        if(!jwt) return

        const requestOptions = {
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/link/principal/${shareId}`, requestOptions)

        if(response.status >= 400) {
            console.log(response.status)
            return
        } 

        const identifierText = await response.text()
        const identifier = JSON.parse(identifierText)
        router.push(`/u/${identifier.author}/${identifier.title}`)
    }

    const getNoteIdentifier = async () => {
        const requestOptions = {
            headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt}
        }
        console.log(shareId)
        const response = await fetch(`http://localhost:8080/api/v1/shares/link/id/${shareId}/note`, requestOptions)

        if(response.status >= 400) {
            console.log(response.status)
            return
        }
        const noteIdentifierText = await response.text()
        const parsedNoteIdentifier = JSON.parse(noteIdentifierText)
        setNoteIdentifier(parsedNoteIdentifier)
    }


    return (
        <div>
            <Head>
                <title>use shared link</title>
            </Head>

            <Navbar />

            <Typography style={{marginTop: '25vh'}}>
                title: {noteIdentifier.title}
            </Typography>

        </div>
    )
}