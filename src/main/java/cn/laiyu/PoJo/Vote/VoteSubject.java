package cn.laiyu.PoJo.Vote;

import cn.laiyu.PoJo.Room.Room;
import cn.laiyu.PoJo.User.Vote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by humac on 2017/7/10.
 */
public class VoteSubject implements VoteThreame {
    private List<Integer> campaignObservers=new CopyOnWriteArrayList<Integer>();

    private List<IObserver> voteObservers=new CopyOnWriteArrayList<IObserver>();

    public List<String> ticTag=new CopyOnWriteArrayList<>();
    @Override
    public void addObservers(IObserver observer) {
        System.out.println(((VoteObserver)observer).mySeatId);
        this.voteObservers.add(observer);

    }

    @Override
    public void deleteObservers(IObserver observer) {

    }

    @Override
    public void notifyAllObservers(String msg) {

    }

    @Override
    public void addCampaignObservers(Integer seatId) {
        this.campaignObservers.add(seatId);
        this.ticTag.add(seatId+"");
    }

    public void eixtCampaignObservers(Integer seatId){ this.campaignObservers.remove(seatId);}

    public List<Integer> getCampaignObservers( ) {
        return campaignObservers;
    }

    public void setCampaignObservers(List<Integer> campaignObservers) {
        this.campaignObservers = campaignObservers;
    }

    public List<IObserver> getVoteObservers( ) {
        return voteObservers;
    }

    public void setVoteObservers(List<IObserver> voteObservers) {
        this.voteObservers = voteObservers;
    }
}
