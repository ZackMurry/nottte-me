import { Grid, Typography } from '@material-ui/core'
import { useEffect, useState } from 'react'
import CreateLinkShare from './CreateLinkShare'
import LinkShareItem from './LinkShareItem'

export default function LinkSharesTable({ jwt, title }) {
    const [ linkShares, setLinkShares ] = useState([])

    const getFromServer = async () => {
        if (!jwt) return

        const requestOptions = {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/link/principal/note/${encodeURI(title)}`, requestOptions)
        if (response.status !== 200) {
            console.log(response.status)
            return
        }
        const text = await response.text()
        console.log(text)
        setLinkShares(JSON.parse(text))
    }

    useEffect(() => {
        if (title) {
            getFromServer()
        }
    }, [ title ])

    const updateLinkShare = (index, newAuthority, newStatus) => {
        const tempLinkShares = linkShares.slice()
        tempLinkShares[index] = {
            ...tempLinkShares[index],
            authority: newAuthority,
            status: newStatus
        }
        setLinkShares(tempLinkShares)
    }

    return (
        <>
            <Typography variant='h4' style={{ textAlign: 'center', marginTop: '5vh', marginBottom: '3vh' }}>
                Link shares
            </Typography>
            <Grid container spacing={3}>
                {
                    linkShares.length !== 0 && (
                        <>
                            <Grid container spacing={3} item xs={12}>
                                <Grid item xs={12} md={6}>
                                    <Typography style={{ fontWeight: 700 }}>
                                        ID
                                    </Typography>
                                </Grid>
                                <Grid item xs={3} md={2}>
                                    <Typography style={{ fontWeight: 700 }}>
                                        Authority
                                    </Typography>
                                </Grid>
                                <Grid item xs={3} md={3}>
                                    <Typography style={{ fontWeight: 700 }}>
                                        Status
                                    </Typography>
                                </Grid>
                                <Grid item xs={3} md={1}>
                                    <Typography style={{ fontWeight: 700 }}>
                                        Edit
                                    </Typography>
                                </Grid>
                            </Grid>
                            {
                                linkShares.map((linkShare, index) => (
                                    <LinkShareItem
                                        id={linkShare.id}
                                        authority={linkShare.authority}
                                        status={linkShare.status}
                                        jwt={jwt}
                                        onUpdate={(newAuthority, newStatus) => updateLinkShare(index, newAuthority, newStatus)}
                                        key={linkShare.id}
                                    />
                                ))
                            }
                        </>
                    )
                }
                {
                    linkShares.length === 0 && (
                        <Grid item xs={12}>
                            <Typography variant='h6' style={{ textAlign: 'center', fontWeight: 400 }}>
                                You don't have any link shares
                            </Typography>
                        </Grid>
                    )
                }
            </Grid>

            <CreateLinkShare
                title={title}
                jwt={jwt}
                onCreate={getFromServer}
            />

        </>

    )
}
