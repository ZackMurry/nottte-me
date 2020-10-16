import Head from 'next/head'
import { useRouter } from 'next/router'
import Cookie from 'js-cookie'
import { Button, Paper, Typography } from '@material-ui/core'
import { useEffect, useState } from 'react'
import Navbar from '../../components/navbar/Navbar'
import parseJwt from '../../components/utils/ParseJwt'

export default function UseLinkShare() {
    const router = useRouter()
    const { shareId } = router.query

    const [ jwt ] = useState(Cookie.get('jwt')) //todo change jwts to this to preserve state

    const [ noteIdentifier, setNoteIdentifier ] = useState({})
    const [ linkShare, setLinkShare ] = useState({})

    const getNoteIdentifier = async () => {
        const requestOptions = {
            headers: { 'Content-Type': 'application/json' }
        }

        console.log(shareId)
        const noteIdentifierResponse = await fetch(`http://localhost:8080/api/v1/shares/link/id/${shareId}/note`, requestOptions)

        if (noteIdentifierResponse.status >= 400) {
            console.log(noteIdentifierResponse.status)
            return
        }
        const noteIdentifierText = await noteIdentifierResponse.text()
        const parsedNoteIdentifier = JSON.parse(noteIdentifierText)
        setNoteIdentifier(parsedNoteIdentifier)

        //seeing if user already has access to not
        if (jwt) {
            const accessRequestOptions = {
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
            }

            const accessResponse = await fetch(
                `http://localhost:8080/api/v1/shares/principal/note/${parsedNoteIdentifier.author}/${parsedNoteIdentifier.title}/access`,
                accessRequestOptions
            )

            if (accessResponse.status < 400) {
                const accessText = await accessResponse.text()
                const parsedAccess = JSON.parse(accessText)

                //using triple equals because this could feasibly be something like "false"
                if (parsedAccess === true) {
                    router.push(`/u/${parsedNoteIdentifier.author}/${parsedNoteIdentifier.title}`)
                }
            }
        }

        const linkShareResponse = await fetch(`http://localhost:8080/api/v1/shares/link/id/${shareId}/share`, requestOptions)
        const linkShareText = await linkShareResponse.text()
        const parsedLinkShare = JSON.parse(linkShareText)
        setLinkShare(parsedLinkShare)

        console.log(parseJwt(jwt)?.sub + '; ' + parsedLinkShare.author)
        if (jwt && parsedLinkShare.author === parseJwt(jwt).sub) {
            router.push(`/n/${encodeURI(parsedNoteIdentifier.title)}`)
        }
    }

    useEffect(() => {
        if (shareId) {
            getNoteIdentifier()
        }
    }, [ shareId ])

    const getAccessToNote = async () => {
        if (!jwt) return

        const requestOptions = {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/link/principal/${shareId}`, requestOptions)

        if (response.status >= 400) {
            console.log(response.status)
            return
        }

        const identifierText = await response.text()
        const identifier = JSON.parse(identifierText)
        router.push(`/u/${identifier.author}/${identifier.title}`)
    }

    return (
        <div>
            <Head>
                <title>{noteIdentifier.title ? noteIdentifier.title : 'nottte.me'}</title>
            </Head>

            <Navbar />

            <Paper
                style={{
                    margin: '0 auto',
                    marginTop: '20vh',
                    marginBottom: '20vh',
                    width: '50%',
                    minHeight: '120vh',
                    paddingBottom: '10vh',
                    borderRadius: 40,
                    boxShadow: '5px 5px 10px black',
                    minWidth: 750
                }}
            >
                <Typography
                    variant='h2'
                    style={{
                        marginTop: '25vh', textAlign: 'center', padding: '3vh', maxWidth: '80%', margin: '0 auto'
                    }}
                >
                    Get access to
{' '}
{noteIdentifier.title ? '"' + noteIdentifier.title + '"' : 'a note'}
                </Typography>
                <Typography style={{ textAlign: 'center' }}>
                    {
                        noteIdentifier.author ? noteIdentifier.author.charAt(0).toUpperCase() + noteIdentifier.author.slice(1) + ' ' : 'A user '
                    }
                    wants to give you
                    {
                        ' ' + (linkShare.authority ? linkShare.authority.toLowerCase() + ' ' : '')
                    }
                    permissions on their note
                    {
                        noteIdentifier.title ? ' "' + noteIdentifier.title + '"' : ''
                    }
                    .
                </Typography>
                {
                    //todo view without being authenticated?
                    jwt
                        ? (
<div style={{ width: '60%', margin: '0 auto' }}>
                            <div style={{ margin: '2vh auto', display: 'flex', justifyContent: 'center' }}>
                                <Button
                                    variant='contained'
                                    onClick={getAccessToNote}
                                    color='secondary'
                                    style={{ margin: '0 auto' }}
                                >
                                    View note
                                </Button>
                            </div>

                            <Typography style={{ textAlign: 'center', fontWeight: 500, fontSize: 18 }}>
                                You'll get access to this note in the same way you could access any other shared note.
                            </Typography>
</div>
                        )
                        : (
<div>
                            <Typography style={{ textAlign: 'center', fontWeight: 500, fontSize: 18 }}>
                                You need to be signed in to get access to this note.
{' '}
<br />
                                Please either
{' '}
{' '}
                                <a href={`/signup?redirect=/l/${shareId}`} style={{ textDecoration: 'underline' }}>
                                    sign up
                                </a>
                                {' '}
{' '}
or
{' '}
                                <a href={`/login?redirect=/l/${shareId}`} style={{ textDecoration: 'underline' }}>
                                    login
                                </a>
                                .
                            </Typography>
</div>
                        )
                }
            </Paper>

        </div>
    )
}
