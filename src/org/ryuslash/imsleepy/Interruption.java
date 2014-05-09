package org.ryuslash.imsleepy;

import java.util.Date;

public class Interruption
{
    private long id;
    private long session_id;
    private Date time;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getSessionId()
    {
        return session_id;
    }

    public void setSessionId(long session_id)
    {
        this.session_id = session_id;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }
}
