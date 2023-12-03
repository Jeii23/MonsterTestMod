package monstermod.monster;

import monstermod.vfx.ColoredSmallLaserEffect;
import monstermod.BasicMod;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.*;

import java.util.Iterator;

public class SpikeMonster extends CustomMonster {
  public static final String ID = BasicMod.makeID(monstermod.monster.SpikeMonster.class.getSimpleName());
  private static final String IMG = BasicMod.imagePath("monster/Spikemonster.png");
  private static final MonsterStrings monsterStrings;
  public static final String NAME;
  public static final String[] MOVES;
  public static final String[] DIALOG;
  private int littleDmg = 3;
  private int bigDmg = 10;

  private int spikeCount = 0;

  private static final byte SPIKE = 1;
  private static final byte OUCH = 2;



  public SpikeMonster(float x, float y) {
    super(NAME, ID, 20, 40.0F, -5.0F, 130.0F, 180.0F, IMG, x, y);

    if (AbstractDungeon.ascensionLevel >= 7) {
      this.setHp(15, 19);
    }
    else {
      this.setHp(14, 18);
    }
    if (AbstractDungeon.ascensionLevel >= 2) {
      this.littleDmg = 5;
      this.bigDmg = 15;
    }
    else {
      this.littleDmg = 3;
      this.bigDmg = 10;
    }


    this.damage.add(new DamageInfo(this, this.bigDmg));
    this.damage.add(new DamageInfo(this, this.littleDmg));
    this.animation = null;
  }

  public void takeTurn() {
    Iterator var1;
    AbstractMonster m;
    int currentMaxHealth =this.currentHealth;
    AbstractMonster spikeCandidate = this;

    switch (this.nextMove) {
      case SPIKE:
        AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
        AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(this, 0.4f, 0.6f));
        var1 = AbstractDungeon.getMonsters().monsters.iterator();

        while(var1.hasNext()) {
          m = (AbstractMonster)var1.next();
          if (!m.isDying && !m.isEscaping) {
            if (currentMaxHealth<m.currentHealth)
              spikeCandidate = m;

          }
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(spikeCandidate, this, new ThornsPower(spikeCandidate, 1), 1));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(1), AttackEffect.SLASH_DIAGONAL));
        break;
      case OUCH:
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0], 1.0F, 2.0F));
        AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
        AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(this, 0.4f, 0.6f));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo) this.damage.get(0), AttackEffect.LIGHTNING));
        break;
    }
    AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
  }

  private void playSfx() {
    int roll = MathUtils.random(1);
    if (roll == 0) {
      AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_1A"));
    } else {
      AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_1B"));
    }

  }

  private void playDeathSfx() {
    int roll = MathUtils.random(2);
    if (roll == 0) {
      AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_2A"));
    } else if (roll == 1) {
      AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_2B"));
    } else {
      AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINDOPEY_2C"));
    }

  }

  protected void getMove(int num) {
    int aliveCount = 0;
    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
      if (!m.isDying && !m.isEscaping) {
        ++aliveCount;
      }
    }
    if (spikeCount < 2){
      this.setMove(SPIKE, Intent.ATTACK_BUFF, littleDmg);
      spikeCount++;
    }
    else {
      this.setMove(OUCH, Intent.ATTACK,bigDmg);
      spikeCount=0;
    }


  }

  public void die() {
    super.die();
    this.playDeathSfx();
  }

  static {
    monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpikeMonster");
    NAME = monsterStrings.NAME;
    MOVES = monsterStrings.MOVES;
    DIALOG = monsterStrings.DIALOG;
  }

}