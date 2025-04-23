import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, ManyToOne, OneToMany } from 'typeorm';
import { User } from './User';
import { Comment } from './Comment';

@Entity('publications')
export class Publication {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  title: string;

  @Column('text')
  content: string;

  @Column({ name: 'image_url' })
  imageUrl: string;

  @Column({ name: 'valid_from' })
  validFrom: Date;

  @Column({ name: 'valid_to' })
  validTo: Date;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @Column({ default: 0 })
  likes: number;

  @Column()
  category: string;

  @Column({
    type: 'enum',
    enum: ['pending', 'published'],
    default: 'pending'
  })
  status: 'pending' | 'published';

  @ManyToOne(() => User, user => user.publications)
  author: User;

  @OneToMany(() => Comment, comment => comment.publication)
  comments: Comment[];
}