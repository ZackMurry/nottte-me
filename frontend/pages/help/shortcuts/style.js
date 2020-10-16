import { Paper, Typography } from '@material-ui/core'
import Navbar from '../../../components/navbar/Navbar'

export default function Style() {
    return (
        <div>
            <Navbar />
            <div style={{ marginTop: '15vh' }}>
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
                    <Typography variant='h1' style={{ padding: '3vh 0 3vh 0', textAlign: 'center' }}>
                        Text shortcuts
                    </Typography>
                    {/* content */}
                    <div style={{ width: '80%', margin: '0 auto' }}>

                    </div>
                </Paper>
            </div>
        </div>
    )
}