import { Typography } from "@material-ui/core";
import Head from 'next/head'
import Navbar from '../navbar/Navbar'
import theme from "../theme";

export default function MobileLandingPage() {
    return (
        <div
            style={{
                margin: 0, padding: 0, overflowX: 'hidden', overflowY: 'hidden', width: '100%'
            }}
        >
            <Head>
                <title>nottte.me</title>
            </Head>

            <Navbar />

            <div style={{ margin: '20vh 0' }}>
                <div>
                    <Typography
                        color='primary'
                        style={{
                            fontSize: '10vh',
                            fontFamily: 'Fjalla One',
                            marginLeft: '7.5vw',
                            marginTop: '10%'
                        }}
                    >
                        take notes.
                    </Typography>
                    <Typography
                        color='primary'
                        style={{
                            fontSize: '4vh',
                            marginLeft: '7.5vw',
                            fontWeight: 300
                        }}
                    >
                        a text editor focused on speed
                    </Typography>

                    {/* todo maybe change into a curve (circly) */}
                    <div>
                        <div
                            style={{
                                backgroundColor: 'white',
                                width: '100%',
                                minWidth: 100,
                                height: '20vh',
                                minHeight: 100,
                                clipPath: 'polygon(100% 0, 100% 100%, 0% 100%)',
                                zIndex: -1,
                                position: 'absolute',
                                top: '60vh',
                                left: 0
                            }}
                        />

                        <div
                            style={{
                                backgroundColor: 'white',
                                width: '100%',
                                minWidth: 100,
                                height: '300vh',
                                minHeight: 100,
                                zIndex: -1,
                                position: 'absolute',
                                top: '74vh',
                                left: 0
                            }}
                        />
                    </div>
                </div>

                {/* content */}
                <div style={{ marginTop: '50vh' }}>
                    <div>
                        <Typography variant='h4' style={{ textAlign: 'center', fontWeight: 700 }}>
                            Customizable shortcuts
                        </Typography>
                        <div
                            style={{
                                height: 5,
                                width: '10vw',
                                minWidth: 100,
                                backgroundColor: theme.palette.secondary.main,
                                marginLeft: '25vw'
                            }}
                        />
                        <div style={{ margin: '3vh 20vw' }}>
                            <Typography>
                                You can create your own keyboard shortcuts to
                                do whatever you need — from inserting text to
                                highlighting your selection.
                            </Typography>
                            {/* todo maybe a svg or something to demonstrate it */}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
